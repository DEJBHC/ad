<template>
  <div>
    <a-row :gutter="[8, 8]">
      <a-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
        <a-card title="概况" :loading="summary.loading" :bordered="false" :body-style="{ padding: '6' }">
          <template #extra><a @click="loadData(summary)"><a-icon type="sync" /></a></template>
          <a-row>
            <template v-for="rec in summary.dataSource">
              <a-col :span="rec.width">
                <head-info :title="rec.label" :content="formatAmt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
      <a-col :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
        <a-card title="客户" :loading="customer.loading" :bordered="false" :body-style="{ padding: '6' }">
          <template #extra><a @click="loadData(customer)"><a-icon type="sync" /></a></template>
          <a-row>
            <template v-for="rec in customer.dataSource">
              <a-col :span="rec.width">
                <head-info :title="rec.label" :content="formatInt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
      <a-col :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
        <a-card title="供应商" :loading="supplier.loading" :bordered="false" :body-style="{ padding: '6' }">
          <template #extra><a @click="loadData(supplier)"><a-icon type="sync" /></a></template>
          <a-row>
            <template v-for="rec in supplier.dataSource">
              <a-col :span="rec.width">
                <head-info :title="rec.label" :content="formatInt(rec.value)" :center="true" />
              </a-col>
            </template>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[8, 8]">
      <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col span="24">
            <a-card title="销售" :loading="sale.loading" :bordered="false" :body-style="{ padding: '0' }">
              <template #extra><a @click="loadData(sale)"><a-icon type="sync" /></a></template>
              <a-row>
                <template v-for="rec in sale.dataSource">
                  <a-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
                    <a-card-grid style="width: 100%" :bordered="true" :body-style="{padding: '0'}">
                      <h4>{{rec.label}}</h4>
                      <a-row>
                        <a-col :span="10" :offset="1">
                          <head-info title="今日笔数" :content="formatInt(rec.t_count)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatInt(rec.w_count)}}</div>
                          <div>本月：{{formatInt(rec.m_count)}}</div>
                        </a-col>
                        <a-col :span="13">
                          <head-info title="今日金额" :content="formatAmt(rec.t_amt)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatAmt(rec.w_amt)}}</div>
                          <div>本月：{{formatAmt(rec.m_amt)}}</div>
                        </a-col>
                      </a-row>
                    </a-card-grid>
                  </a-col>
                </template>
              </a-row>
            </a-card>
          </a-col>

          <a-col span="24">
            <a-card title="采购" :loading="purchase.loading" :bordered="false" :body-style="{ padding: '0' }">
              <template #extra><a @click="loadData(sale)"><a-icon type="sync" /></a></template>
              <a-row>
                <template v-for="rec in purchase.dataSource">
                  <a-col :xl="12" :lg="12" :md="12" :sm="24" :xs="24">
                    <a-card-grid style="width: 100%" :bordered="true" :body-style="{padding: '0'}">
                      <h4>{{rec.label}}</h4>
                      <a-row>
                        <a-col :span="10" :offset="1">
                          <head-info title="今日笔数" :content="formatInt(rec.t_count)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatInt(rec.w_count)}}</div>
                          <div>本月：{{formatInt(rec.m_count)}}</div>
                        </a-col>
                        <a-col :span="13">
                          <head-info title="今日金额" :content="formatAmt(rec.t_amt)" :center="false"/>
                          <div style="margin-top: 16px">本周：{{formatAmt(rec.w_amt)}}</div>
                          <div>本月：{{formatAmt(rec.m_amt)}}</div>
                        </a-col>
                      </a-row>
                    </a-card-grid>
                  </a-col>
                </template>
              </a-row>
            </a-card>
          </a-col>
        </a-row>
      </a-col>

      <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col :span="24">
            <a-card title="销售金额" :loading="saleAmt.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra><a @click="loadData(saleAmt)"><a-icon type="sync" /></a></template>
              <bar :dataSource="saleAmt.dataSource" :height="136" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="毛利润" :loading="saleProfit.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra><a @click="loadData(saleProfit)"><a-icon type="sync" /></a></template>
              <bar :dataSource="saleProfit.dataSource" :height="134" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="采购金额" :loading="purchaseAmt.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra><a @click="loadData(purchaseAmt)"><a-icon type="sync" /></a></template>
              <bar :dataSource="purchaseAmt.dataSource":height="134" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
          <a-col :span="24">
            <a-card title="库存结存金额" :loading="stockBalCost.loading" :bordered="false" :body-style="{padding: '0'}">
              <template #extra><a @click="loadData(stockBalCost)"><a-icon type="sync" /></a></template>
              <bar :dataSource="stockBalCost.dataSource" :height="136" style="padding: 0 8px 0 8px"/>
            </a-card>
          </a-col>
        </a-row>
      </a-col>

      <a-col :xl="6" :lg="12" :md="12" :sm="24" :xs="24">
        <a-row :gutter="[8, 8]">
          <a-col :span="24">
            <a-card title="处理中主要单据" :loading="doingBill.loading" :bordered="false" :body-style="{ padding: '0' }">
              <template #extra><a @click="loadData(doingBill)"><a-icon type="sync" /></a></template>
              <a-table
                ref="table"
                size="middle"
                style="height:832px"
                :bordered="false"
                rowKey="name"
                :scroll="{x: true}"
                :columns="doingBill.columns"
                :dataSource="doingBill.dataSource"
                :pagination="false"
                class="j-table-force-nowrap"/>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
    </a-row>

   </div>
</template>

<script>
  import Bar from '@/components/chart/Bar'
  import HeadInfo from '@/components/tools/HeadInfo'
  import Radar from '@/components/chart/Radar'
  import '@/assets/less/TableExpand.less'
  import XEUtils from "xe-utils";

  const barData = []
  for (let i = 0; i < 12; i += 1) {
    barData.push({
      x: `${i + 1}月`,
      y: Math.floor(Math.random() * 1000) + 200
    })
  }

  export default {
    name: "Analysis",
    components: { HeadInfo, Radar,Bar },
    data() {
      return {
        summary: {
          url: '/wrapper/cgreport/getData/1579848144423751681',
          loading: false,
          dataSource: [],
        },
        customer: {
          url: '/wrapper/cgreport/getData/1580740609854935042',
          loading: false,
          dataSource: [],
        },
        supplier: {
          url: '/wrapper/cgreport/getData/1580740795561938946',
          loading: false,
          dataSource: [],
        },
        sale: {
          url: '/wrapper/cgreport/getData/1579758928603910145',
          loading: false,
          dataSource: [],
        },
        purchase: {
          url: '/wrapper/cgreport/getData/1579760837406494722',
          loading: false,
          dataSource: [],
        },
        saleAmt: {
          url: '/wrapper/cgreport/getData/1580915783778701314',
          loading: false,
          dataSource: [],
        },
        saleProfit: {
          url: '/wrapper/cgreport/getData/1580918054310645761',
          loading: false,
          dataSource: [],
        },
        purchaseAmt: {
          url: '/wrapper/cgreport/getData/1580918550912045057',
          loading: false,
          dataSource: [],
        },
        stockBalCost: {
          url: '/wrapper/cgreport/getData/1580923428333948929',
          loading: false,
          dataSource: [],
        },

        doingBill: {
          url: '/wrapper/cgreport/getData/1582194686237454338?pageSize=100',
          loading: false,
          dataSource: [{class:'销售订单'},{class:'采购订单'},{class:'出库单'},{class:'入库单'}],
          columns: [
            {
              title:'',
              key: 'name',
              align:"center",
              dataIndex: 'name',
            },
            {
              title:'编制中',
              align:"center",
              dataIndex: 'edit',
            },
            {
              title:'待核批',
              align:"center",
              dataIndex: 'appr',
            },
            {
              title:'执行中',
              align:"center",
              dataIndex: 'exec',
            },
          ],
        }
      }
    },

    created() {
      this.loadData(this.summary);
      this.loadData(this.customer);
      this.loadData(this.supplier);
      this.loadData(this.sale);
      this.loadData(this.purchase);
      this.loadData(this.saleAmt);
      this.loadData(this.saleProfit);
      this.loadData(this.purchaseAmt);
      this.loadData(this.stockBalCost);
      this.loadData(this.doingBill);
    },

    methods: {
      loadData(data) {
        data.loading = true;
        this.$http.get(data.url)
          .then(res => data.dataSource =  res.result.records)
          .finally(() => data.loading = false);
      },

      formatAmt(amt) {
        return XEUtils.commafy(amt, {digits: 2}).toString();
      },

      formatInt(i) {
        return XEUtils.commafy(i, {digits: 0}).toString();
      }
    }
  }
</script>

<style lang="less" scoped>
</style>