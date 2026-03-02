<template>
  <div class="division-selector">
    <!-- 树形选择模式 -->
    <div v-if="mode === 'tree'" class="tree-select-wrapper">
      <el-popover
        placement="bottom-start"
        width="300"
        trigger="click"
        v-model="treeVisible"
        :disabled="disabled"
      >
        <el-tree
          ref="tree"
          :data="treeData"
          :props="treeProps"
          :node-key="'divisionCode'"
          :default-expand-all="false"
          :expand-on-click-node="false"
          :check-on-click-node="true"
          :filter-node-method="filterNode"
          @node-click="handleTreeNodeClick"
          class="division-tree"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <span class="tree-node-label">
              <i :class="getLevelIcon(data.divisionLevel)"></i>
              {{ data.divisionName }}
            </span>
          </span>
        </el-tree>
        
        <el-input
          slot="reference"
          v-model="displayValue"
          :placeholder="placeholder"
          :disabled="disabled"
          :clearable="clearable"
          @clear="handleClear"
          @input="handleFilter"
          :readonly="!filterable"
          suffix-icon="el-icon-arrow-down"
          style="width: 100%"
        />
      </el-popover>
    </div>
    
    <!-- 级联选择模式 -->
    <el-cascader
      v-else-if="mode === 'cascader'"
      v-model="cascaderValue"
      :options="cascaderData"
      :props="cascaderProps"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :filterable="filterable"
      :show-all-levels="showAllLevels"
      @change="handleCascaderChange"
      style="width: 100%"
    />
    
    <!-- 下拉选择模式 -->
    <el-select
      v-else
      v-model="selectedValue"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :filterable="filterable"
      :multiple="multiple"
      :collapse-tags="multiple"
      @change="handleChange"
      style="width: 100%"
    >
      <el-option
        v-for="item in selectOptions"
        :key="item.divisionCode"
        :label="item.divisionName"
        :value="item.divisionCode"
      />
    </el-select>
  </div>
</template>

<script>
import { getDivisionTree, getDirectChildren } from "@/api/shebao/administrativeDivision"

export default {
  name: "DivisionSelector",
  props: {
    // 选择器值
    value: {
      type: [String, Array],
      default: null
    },
    // 选择模式：tree(树形选择), cascader(级联选择), select(下拉选择)
    mode: {
      type: String,
      default: 'tree',
      validator: value => ['tree', 'cascader', 'select'].includes(value)
    },
    // 使用场景：parent(父级选择,显示1-4级), village(村级选择,只能选择村级)
    scenario: {
      type: String,
      default: 'parent',
      validator: value => ['parent', 'village'].includes(value)
    },
    // 最大显示级别
    maxLevel: {
      type: Number,
      default: null
    },
    // 是否只显示有子级的节点
    onlyWithChildren: {
      type: Boolean,
      default: false
    },
    // 占位符
    placeholder: {
      type: String,
      default: '请选择行政区划'
    },
    // 是否可清空
    clearable: {
      type: Boolean,
      default: true
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否可过滤
    filterable: {
      type: Boolean,
      default: true
    },
    // 是否多选
    multiple: {
      type: Boolean,
      default: false
    },
    // 树形选择是否父子不关联
    checkStrictly: {
      type: Boolean,
      default: true
    },
    // 级联选择是否显示完整路径
    showAllLevels: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      selectedValue: null,
      cascaderValue: [],
      treeData: [],
      cascaderData: [],
      selectOptions: [],
      treeVisible: false,
      displayValue: '',
      filterText: '',
      treeProps: {
        children: 'children',
        label: 'divisionName'
      },
      cascaderProps: {
        children: 'children',
        label: 'divisionName',
        value: 'divisionCode',
        leaf: 'leaf'
      }
    }
  },
  computed: {
    // 根据使用场景计算最大级别
    computedMaxLevel() {
      if (this.maxLevel) {
        return this.maxLevel
      }
      return this.scenario === 'parent' ? 4 : 5
    },
    // 根据使用场景计算是否只显示有子级的节点
    computedOnlyWithChildren() {
      if (this.scenario === 'village') {
        return true
      }
      return this.onlyWithChildren
    }
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.mode === 'cascader') {
          this.cascaderValue = newVal || []
        } else {
          this.selectedValue = newVal
        }
        this.updateDisplayValue()
      },
      immediate: true
    },
    scenario: {
      handler() {
        this.loadData()
      },
      immediate: true
    },
    // 监听组件可见性，每次显示时重新加载数据
    treeVisible: {
      handler(visible) {
        if (visible && this.mode === 'tree') {
          this.loadTreeData()
        }
      }
    },
    filterText(val) {
      this.$refs.tree && this.$refs.tree.filter(val)
    }
  },
  methods: {
    // 加载数据
    loadData() {
      if (this.mode === 'tree') {
        this.loadTreeData()
      } else if (this.mode === 'cascader') {
        this.loadCascaderData()
      } else {
        this.loadSelectData()
      }
    },
    
    // 加载树形数据
    loadTreeData() {
      getDivisionTree({
        maxLevel: this.computedMaxLevel,
        onlyWithChildren: this.computedOnlyWithChildren
      }).then(response => {
        this.treeData = response.data
        // 数据加载完成后更新显示值
        this.$nextTick(() => {
          this.updateDisplayValue()
        })
      })
    },
    
    // 加载级联数据
    loadCascaderData() {
      getDivisionTree({
        maxLevel: this.computedMaxLevel,
        onlyWithChildren: this.computedOnlyWithChildren
      }).then(response => {
        this.cascaderData = this.formatCascaderData(response.data)
      })
    },
    
    // 加载下拉数据
    loadSelectData() {
      if (this.scenario === 'village') {
        // 村级选择场景，只显示村级
        getDivisionTree({
          maxLevel: 5,
          onlyWithChildren: false
        }).then(response => {
          this.selectOptions = this.extractVillageNodes(response.data)
        })
      } else {
        // 父级选择场景，显示1-4级
        getDivisionTree({
          maxLevel: 4,
          onlyWithChildren: false
        }).then(response => {
          this.selectOptions = this.flattenTreeData(response.data)
        })
      }
    },
    
    // 格式化级联数据，添加leaf标识
    formatCascaderData(data) {
      return data.map(item => {
        const formattedItem = { ...item }
        if (item.children && item.children.length > 0) {
          formattedItem.children = this.formatCascaderData(item.children)
          formattedItem.leaf = false
        } else {
          formattedItem.leaf = true
        }
        return formattedItem
      })
    },
    
    // 提取村级节点
    extractVillageNodes(data) {
      let villages = []
      
      const extractRecursive = (nodes) => {
        nodes.forEach(node => {
          if (node.divisionLevel === 5) {
            villages.push(node)
          }
          if (node.children && node.children.length > 0) {
            extractRecursive(node.children)
          }
        })
      }
      
      extractRecursive(data)
      return villages
    },
    
    // 扁平化树形数据
    flattenTreeData(data) {
      let flattened = []
      
      const flattenRecursive = (nodes) => {
        nodes.forEach(node => {
          flattened.push(node)
          if (node.children && node.children.length > 0) {
            flattenRecursive(node.children)
          }
        })
      }
      
      flattenRecursive(data)
      return flattened
    },
    
    // 处理选择变化
    handleChange(value) {
      this.$emit('input', value)
      this.$emit('change', value)
    },
    
    // 处理级联选择变化
    handleCascaderChange(value) {
      const selectedValue = value && value.length > 0 ? value[value.length - 1] : null
      this.$emit('input', selectedValue)
      this.$emit('change', selectedValue, value)
    },

    // 处理树节点点击
    handleTreeNodeClick(data) {
      this.selectedValue = data.divisionCode
      this.displayValue = data.fullName || data.divisionName
      this.treeVisible = false
      this.$emit('input', data.divisionCode)
      this.$emit('change', data.divisionCode, data)
    },

    // 处理清空
    handleClear() {
      this.selectedValue = null
      this.displayValue = ''
      this.filterText = ''
      // 清空树形过滤
      if (this.$refs.tree) {
        this.$refs.tree.filter('')
      }
      this.$emit('input', null)
      this.$emit('change', null)
    },

    // 处理过滤输入
    handleFilter(value) {
      this.filterText = value
      // 如果是搜索模式且输入框可编辑，则进行搜索
      if (this.filterable && !this.treeVisible) {
        // 如果输入内容，自动打开下拉
        if (value && value.trim()) {
          this.treeVisible = true
        }
      }
    },

    // 树形过滤方法
    filterNode(value, data) {
      if (!value) return true
      // 支持按名称和全路径名称搜索
      const searchText = value.toLowerCase()
      const divisionName = (data.divisionName || '').toLowerCase()
      const fullName = (data.fullName || '').toLowerCase()
      
      return divisionName.indexOf(searchText) !== -1 || fullName.indexOf(searchText) !== -1
    },

    // 更新显示值
    updateDisplayValue() {
      if (!this.selectedValue) {
        this.displayValue = ''
        return
      }
      
      // 从树形数据中找到对应的节点
      const node = this.findNodeByCode(this.treeData, this.selectedValue)
      if (node) {
        this.displayValue = node.fullName || node.divisionName
      }
    },

    // 递归查找节点
    findNodeByCode(nodes, code) {
      for (let node of nodes) {
        if (node.divisionCode === code) {
          return node
        }
        if (node.children && node.children.length > 0) {
          const found = this.findNodeByCode(node.children, code)
          if (found) return found
        }
      }
      return null
    },

    // 获取级别图标
    getLevelIcon(level) {
      const icons = {
        1: 'el-icon-office-building', // 省
        2: 'el-icon-school',          // 市
        3: 'el-icon-house',           // 县
        4: 'el-icon-map-location',    // 乡镇
        5: 'el-icon-location'         // 村
      }
      return icons[level] || 'el-icon-folder'
    },

    // 刷新数据（供外部调用）
    refresh() {
      this.loadData()
    }
  }
}
</script>

<style scoped>
.division-selector {
  width: 100%;
}

.tree-select-wrapper {
  width: 100%;
}

.division-tree {
  max-height: 300px;
  overflow-y: auto;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-node-label {
  display: flex;
  align-items: center;
}

.tree-node-label i {
  margin-right: 6px;
  color: #909399;
}

.tree-node-label i.el-icon-office-building {
  color: #f56c6c; /* 省 - 红色 */
}

.tree-node-label i.el-icon-school {
  color: #e6a23c; /* 市 - 橙色 */
}

.tree-node-label i.el-icon-house {
  color: #909399; /* 县 - 灰色 */
}

.tree-node-label i.el-icon-map-location {
  color: #67c23a; /* 乡镇 - 绿色 */
}

.tree-node-label i.el-icon-location {
  color: #409eff; /* 村 - 蓝色 */
}

/* 树节点悬停效果 */
.division-tree .el-tree-node__content:hover {
  background-color: #f5f7fa;
}

/* 树节点选中效果 */
.division-tree .el-tree-node.is-current > .el-tree-node__content {
  background-color: #ecf5ff;
  color: #409eff;
}

/* Popover样式调整 */
.division-tree >>> .el-tree-node__content {
  height: 32px;
  line-height: 32px;
}

.division-tree >>> .el-tree-node__expand-icon {
  color: #c0c4cc;
}

.division-tree >>> .el-tree-node__expand-icon.expanded {
  transform: rotate(90deg);
}

/* 搜索高亮样式 */
.division-tree >>> .el-tree-node__content .tree-node-label {
  transition: all 0.3s ease;
}

.division-tree >>> .el-tree-node.is-current .tree-node-label {
  font-weight: 500;
}

/* 输入框搜索状态样式 */
.tree-select-wrapper .el-input.is-focus .el-input__inner {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 过滤后的树节点动画 */
.division-tree >>> .el-tree-node {
  transition: opacity 0.3s ease;
}

.division-tree >>> .el-tree-node.is-hidden {
  opacity: 0.3;
}
</style>
