package org.jeecg.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 字典aop类
 * @Author: dangzhenghui
 * @Date: 2019-3-17 21:50
 * @Version: 1.0
 */
@Aspect
@Order(9) //20220912 cfm add：确保DisctAspect Around处理在其它会改变result类型的Aspect的处理前执行。
@Component
@Slf4j
public class DictAspect {
    @Lazy
    @Autowired
    private CommonAPI commonApi;
    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String JAVA_UTIL_DATE = "java.util.Date";

    /**
     * 定义切点Pointcut
     */
    @Pointcut("execution(public * org.jeecg.modules..*.*Controller.*(..)) || @annotation(org.jeecg.common.aspect.annotation.AutoDict)"
            + "||execution(public * io.finer..*.*Controller.*(..))") //20200325 cfm modify
    public void excudeService() {
    }

    //20220105 cfm add
    @Pointcut("execution(public * io.finer..*.*Controller.query*(..))")
    public void queryPointcut() {
    }

    //20220105 cfm: 处理IPage数据
    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long time1=System.currentTimeMillis();
        Object result = pjp.proceed();
        long time2=System.currentTimeMillis();
        log.debug("获取JSON数据 耗时："+(time2-time1)+"ms");
        long start=System.currentTimeMillis();

        //20220913 cfm modi
        //this.parseDictText(result);
        if (result instanceof Result) {
            Object obj = ((Result<?>) result).getResult();
            if (obj instanceof IPage) {
                List<Object> records = ((IPage) obj).getRecords();
                if (checkHasDict(records)) {
                    ((IPage) obj).setRecords(this.parseDictText(records));
                }
            }
        }

        long end=System.currentTimeMillis();
        log.debug("注入字典到JSON数据  耗时"+(end-start)+"ms");
        return result;
    }

    //20220913 cfm add: 处理url为".../dictText"、非IPage数据
    @AfterReturning(returning = "result", pointcut = "queryPointcut()")
    public void doAfterReturning(Object result) throws Throwable {
        String uri = SpringContextUtils.getHttpServletRequest().getRequestURI();
        if (!uri.endsWith("/dictText") || !(result instanceof Result)) {
            return;
        }

        Object obj = ((Result<?>) result).getResult();
        if (obj instanceof IPage) {
            return;
        }

        if (obj instanceof List<?>){
            List<?> records = (List<?>)obj;
            if (checkHasDict(records)) {
                List<JSONObject> jsonObjList = this.parseDictText(records);
                ((Result) result).setResult(jsonObjList);
            }
        } else {
            List<Object> records = Arrays.asList(obj);
            if (checkHasDict(records)) {
                List<JSONObject> jsonObjList = this.parseDictText(records);
                ((Result) result).setResult(jsonObjList.get(0));
            }
        }
    }

    /**
     * 本方法针对返回对象为Result 的IPage的分页列表数据进行动态字典注入
     * 字典注入实现 通过对实体类添加注解@dict 来标识需要的字典内容,字典分为单字典code即可 ，table字典 code table text配合使用与原来jeecg的用法相同
     * 示例为SysUser   字段为sex 添加了注解@Dict(dicCode = "sex") 会在字典服务立马查出来对应的text 然后在请求list的时候将这个字典text，已字段名称加_dictText形式返回到前端
     * 例输入当前返回值的就会多出一个sex_dictText字段
     * {
     *      sex:1,
     *      sex_dictText:"男"
     * }
     * 前端直接取值sext_dictText在table里面无需再进行前端的字典转换了
     *  customRender:function (text) {
     *               if(text==1){
     *                 return "男";
     *               }else if(text==2){
     *                 return "女";
     *               }else{
     *                 return text;
     *               }
     *             }
     *             目前vue是这么进行字典渲染到table上的多了就很麻烦了 这个直接在服务端渲染完成前端可以直接用
     * @param result
     */
    //20220913 cfm modi：
    //  1、增加单记录、记录列表、子记录列表字段的数据进行动态字典注入；
    //  2、代码优化
    //  3、private to public
    public List<JSONObject> parseDictText(List<?> records) throws Exception {
        List<JSONObject> jsonObjs = new ArrayList<>();

        //step.1 筛选出加了 Dict 注解的字段列表
        List<Field> dictFields = new ArrayList<>();
        // 字典数据列表， key = 字典code，value=数据列表
        Map<String, List<String>> dictFieldValuesMap = new HashMap<>(5);

        for (Object record : records) {
            String jsonStr="{}";
            try {
                //update-begin--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
                //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                jsonStr = objectMapper.writeValueAsString(record);
                //update-end--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
            } catch (JsonProcessingException e) {
                log.error("json解析失败"+e.getMessage(),e);
            }
            //update-begin--Author:scott -- Date:20211223 ----for：【issues/3303】restcontroller返回json数据后key顺序错乱 -----
            JSONObject jsonObj = JSONObject.parseObject(jsonStr, Feature.OrderedField);
            //update-end--Author:scott -- Date:20211223 ----for：【issues/3303】restcontroller返回json数据后key顺序错乱 -----

            //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
            //for (Field field : record.getClass().getDeclaredFields()) {
            // 遍历所有字段，把字典Code取出来，放到 map 里
            for (Field field : oConvertUtils.getAllFields(record)) {
                //20220913 cfm：递归解析子记录列表字段
                if (List.class.isAssignableFrom(field.getType())) {
                    String n = field.getName();
                    n = n.substring(0, 1).toUpperCase() + n.substring(1);
                    Method m = record.getClass().getMethod("get" + n);
                    Object obj = m.invoke(record);
                    if (obj instanceof List) {
                        List<?> subRecords = (List<?>) obj;
                        List<JSONObject> subJsonObjs = parseDictText(subRecords);//递归
                        jsonObj.put(field.getName(), subJsonObjs);
                    }
                }

                String value = jsonObj.getString(field.getName());
                if (oConvertUtils.isEmpty(value)) {
                    continue;
                }
                //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------

                if (field.getAnnotation(Dict.class) != null) {
                    if (!dictFields.contains(field)) {
                        dictFields.add(field);
                    }

                    String dicCode = field.getAnnotation(Dict.class).dicCode();
                    String dicText = field.getAnnotation(Dict.class).dicText();
                    String dictTable = field.getAnnotation(Dict.class).dictTable();
                    if (!StringUtils.isEmpty(dictTable)) {
                        dicCode = String.format("%s,%s,%s", dictTable, dicText, dicCode);
                    }

                    List<String> values = dictFieldValuesMap.computeIfAbsent(dicCode, k -> new ArrayList<>());
                    this.listAddAllDeduplicate(values, Arrays.asList(value.split(",")));
                }

                //date类型默认转换string格式化日期
                //update-begin--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
                //if (JAVA_UTIL_DATE.equals(field.getType().getName())&&field.getAnnotation(JsonFormat.class)==null&&item.get(field.getName())!=null){
                //SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                //}
                //update-end--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
            }
            jsonObjs.add(jsonObj);
        }

        //step.2 调用翻译方法，一次性翻译
        Map<String, List<DictModel>> dictsMap = this.translateAllDict(dictFieldValuesMap);

        //step.3 将翻译结果填充到返回结果里
        for (JSONObject record : jsonObjs) {
            for (Field field : dictFields) {
                String dicCode = field.getAnnotation(Dict.class).dicCode();
                String dicText = field.getAnnotation(Dict.class).dicText();
                String dictTable = field.getAnnotation(Dict.class).dictTable();
                if (!StringUtils.isEmpty(dictTable)) {
                    dicCode = String.format("%s,%s,%s", dictTable, dicText, dicCode);
                }

                String value = record.getString(field.getName());
                if (oConvertUtils.isNotEmpty(value)) {
                    List<DictModel> dictModels = dictsMap.get(dicCode);
                    if(dictModels!=null && dictModels.size()>0){
                        String text = this.translDictText(dictModels, value);
                        record.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, text);
                    }
                }
            }
        }

        return jsonObjs;
    }

    /**
     * list 去重添加
     */
    private void listAddAllDeduplicate(List<String> dataList, List<String> addList) {
        // 筛选出dataList中没有的数据
        List<String> filterList = addList.stream().filter(i -> !dataList.contains(i)).collect(Collectors.toList());
        dataList.addAll(filterList);
    }

    /**
     * 一次性把所有的字典都翻译了
     * 1.  所有的普通数据字典的所有数据只执行一次SQL
     * 2.  表字典相同的所有数据只执行一次SQL
     * @param dataListMap
     * @return
     */
    private Map<String, List<DictModel>> translateAllDict(Map<String, List<String>> dataListMap) {
        // 翻译后的字典文本，key=dictCode
        Map<String, List<DictModel>> translText = new HashMap<>(5);
        // 需要翻译的数据（有些可以从redis缓存中获取，就不走数据库查询）
        List<String> needTranslData = new ArrayList<>();
        //step.1 先通过redis中获取缓存字典数据
        for (String dictCode : dataListMap.keySet()) {
            List<String> dataList = dataListMap.get(dictCode);
            if (dataList.size() == 0) {
                continue;
            }
            // 表字典需要翻译的数据
            List<String> needTranslDataTable = new ArrayList<>();
            for (String s : dataList) {
                String data = s.trim();
                if (data.length() == 0) {
                    continue; //跳过循环
                }
                if (dictCode.contains(",")) {
                    String keyString = String.format("sys:cache:dictTable::SimpleKey [%s,%s]", dictCode, data);
                    if (redisTemplate.hasKey(keyString)) {
                        try {
                            String text = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                            List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                            list.add(new DictModel(data, text));
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else if (!needTranslDataTable.contains(data)) {
                        // 去重添加
                        needTranslDataTable.add(data);
                    }
                } else {
                    String keyString = String.format("sys:cache:dict::%s:%s", dictCode, data);
                    if (redisTemplate.hasKey(keyString)) {
                        try {
                            String text = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                            List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                            list.add(new DictModel(data, text));
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    } else if (!needTranslData.contains(data)) {
                        // 去重添加
                        needTranslData.add(data);
                    }
                }

            }
            //step.2 调用数据库翻译表字典
            if (needTranslDataTable.size() > 0) {
                String[] arr = dictCode.split(",");
                String table = arr[0], text = arr[1], code = arr[2];
                String values = String.join(",", needTranslDataTable);
                log.debug("translateDictFromTableByKeys.dictCode:" + dictCode);
                log.debug("translateDictFromTableByKeys.values:" + values);
                List<DictModel> texts = commonApi.translateDictFromTableByKeys(table, text, code, values);
                log.debug("translateDictFromTableByKeys.result:" + texts);
                List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                list.addAll(texts);

                // 做 redis 缓存
                for (DictModel dict : texts) {
                    String redisKey = String.format("sys:cache:dictTable::SimpleKey [%s,%s]", dictCode, dict.getValue());
                    try {
                        // update-begin-author:taoyan date:20211012 for: 字典表翻译注解缓存未更新 issues/3061
                        // 保留5分钟
                        redisTemplate.opsForValue().set(redisKey, dict.getText(), 300, TimeUnit.SECONDS);
                        // update-end-author:taoyan date:20211012 for: 字典表翻译注解缓存未更新 issues/3061
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }

        //step.3 调用数据库进行翻译普通字典
        if (needTranslData.size() > 0) {
            List<String> dictCodeList = Arrays.asList(dataListMap.keySet().toArray(new String[]{}));
            // 将不包含逗号的字典code筛选出来，因为带逗号的是表字典，而不是普通的数据字典
            List<String> filterDictCodes = dictCodeList.stream().filter(key -> !key.contains(",")).collect(Collectors.toList());
            String dictCodes = String.join(",", filterDictCodes);
            String values = String.join(",", needTranslData);
            log.debug("translateManyDict.dictCodes:" + dictCodes);
            log.debug("translateManyDict.values:" + values);
            Map<String, List<DictModel>> manyDict = commonApi.translateManyDict(dictCodes, values);
            log.debug("translateManyDict.result:" + manyDict);
            for (String dictCode : manyDict.keySet()) {
                List<DictModel> list = translText.computeIfAbsent(dictCode, k -> new ArrayList<>());
                List<DictModel> newList = manyDict.get(dictCode);
                list.addAll(newList);

                // 做 redis 缓存
                for (DictModel dict : newList) {
                    String redisKey = String.format("sys:cache:dict::%s:%s", dictCode, dict.getValue());
                    try {
                        redisTemplate.opsForValue().set(redisKey, dict.getText());
                    } catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }
        return translText;
    }

    /**
     * 字典值替换文本
     *
     * @param dictModels
     * @param values
     * @return
     */
    private String translDictText(List<DictModel> dictModels, String values) {
        List<String> result = new ArrayList<>();

        // 允许多个逗号分隔，允许传数组对象
        String[] splitVal = values.split(",");
        for (String val : splitVal) {
            String dictText = val;
            for (DictModel dict : dictModels) {
                if (val.equals(dict.getValue())) {
                    dictText = dict.getText();
                    break;
                }
            }
            result.add(dictText);
        }
        return String.join(",", result);
    }

    /**
     *  翻译字典文本
     * @param code
     * @param text
     * @param table
     * @param key
     * @return
     */
    @Deprecated
    private String translateDictValue(String code, String text, String table, String key) {
    	if(oConvertUtils.isEmpty(key)) {
    		return null;
    	}
        StringBuffer textValue=new StringBuffer();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : "+ k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }
            //update-begin--Author:scott -- Date:20210531 ----for： !56 优化微服务应用下存在表字段需要字典翻译时加载缓慢问题-----
            if (!StringUtils.isEmpty(table)){
                log.debug("--DictAspect------dicTable="+ table+" ,dicText= "+text+" ,dicCode="+code);
                String keyString = String.format("sys:cache:dictTable::SimpleKey [%s,%s,%s,%s]",table,text,code,k.trim());
                    if (redisTemplate.hasKey(keyString)){
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }
                }else {
                    tmpValue= commonApi.translateDictFromTable(table,text,code,k.trim());
                }
            }else {
                String keyString = String.format("sys:cache:dict::%s:%s",code,k.trim());
                if (redisTemplate.hasKey(keyString)){
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                       log.warn(e.getMessage());
                    }
                }else {
                    tmpValue = commonApi.translateDict(code, k.trim());
                }
            }
            //update-end--Author:scott -- Date:20210531 ----for： !56 优化微服务应用下存在表字段需要字典翻译时加载缓慢问题-----

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }

    /**
     * 检测返回结果集中是否包含Dict注解
     * @param records
     * @return
     */
    private Boolean checkHasDict(List<?> records) throws Exception {
        if(oConvertUtils.isNotEmpty(records) && records.size()>0){
            for (Field field : oConvertUtils.getAllFields(records.get(0))) {
                if (oConvertUtils.isNotEmpty(field.getAnnotation(Dict.class))) {
                    return true;
                }

                //20220913 cfm：递归解析子记录列表字段
                if (List.class.isAssignableFrom(field.getType())) {
                    String n = field.getName();
                    n = n.substring(0, 1).toUpperCase() + n.substring(1);
                    Method m = records.get(0).getClass().getMethod("get" + n);
                    Object obj = m.invoke(records.get(0));
                    if (obj instanceof List) {
                        List<?> subRecords = (List<?>) obj;
                        if (checkHasDict(subRecords)) {//递归
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
