import Vue from "vue";
import VueDraggableResizable from "vue-draggable-resizable";

Vue.component("vue-draggable-resizable", VueDraggableResizable);
function initDrag(columns) {
  const draggingMap = {};
  columns.forEach((col) => {
    draggingMap[col.key] = col.width;
  });
  const draggingState = Vue.observable(draggingMap);
  return (h, props, children) => {
    let thDom = null;

    const { key, ...restProps } = props;
    let col;
    if (key === "selection-column") {
      //表格加了复选框，不加这个判断col会是undefided
      col = {};
    } else {
      col = columns.find((col) => {
        const k = col.dataIndex || col.key;
        return k === key;
      });
    }
    if (!col || !col.width) {
      return <th {...restProps}>{children}</th>;
    }
    const onDrag = (x) => {
      draggingState[key] = 0;
      col.width = Math.max(x, 1);
    };

    const onDragstop = () => {
      draggingState[key] = thDom.getBoundingClientRect().width;
    };
    return (
      <th
        {...restProps}
        v-ant-ref={(r) => (thDom = r)}
        width={col.width}
        class="resize-table-th"
      >
        {children}
        <vue-draggable-resizable
          key={col.key}
          class="table-draggable-handle"
          w={10}
          x={draggingState[key] || col.width}
          z={1}
          axis="x"
          draggable={true}
          resizable={false}
          onDragging={onDrag}
          onDragstop={onDragstop}
        ></vue-draggable-resizable>
      </th>
    );
  };
}
export default {
  methods: {
    drag(columns) {
      return {
        header: {
          cell: initDrag(columns),
        },
      };
    },
  },
};