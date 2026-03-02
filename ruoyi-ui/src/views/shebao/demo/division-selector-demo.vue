<template>
  <div class="app-container">
    <div class="demo-section">
      <h3>行政区划选择组件演示</h3>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="demo-item">
            <h4>树形选择模式 - 父级选择（支持搜索）</h4>
            <p>显示1-4级行政区划，支持按名称搜索</p>
            <DivisionSelector
              v-model="treeParentValue"
              mode="tree"
              scenario="parent"
              placeholder="请选择父级行政区划（可搜索）"
              filterable
              clearable
              @change="handleTreeParentChange"
            />
            <div class="result">
              选中值: {{ treeParentValue || '无' }}
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="demo-item">
            <h4>级联选择模式</h4>
            <p>逐级选择行政区划</p>
            <DivisionSelector
              v-model="cascaderValue"
              mode="cascader"
              scenario="parent"
              placeholder="请选择行政区划"
              clearable
              @change="handleCascaderChange"
            />
            <div class="result">
              选中值: {{ cascaderValue || '无' }}
            </div>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="demo-item">
            <h4>下拉选择模式 - 村级选择</h4>
            <p>只显示村级行政区划</p>
            <DivisionSelector
              v-model="selectVillageValue"
              mode="select"
              scenario="village"
              placeholder="请选择村级行政区划"
              clearable
              @change="handleSelectVillageChange"
            />
            <div class="result">
              选中值: {{ selectVillageValue || '无' }}
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-divider></el-divider>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="demo-item">
            <h4>树形选择 - 村级场景</h4>
            <p>只显示有村级的上级行政区划</p>
            <DivisionSelector
              v-model="treeVillageValue"
              mode="tree"
              scenario="village"
              placeholder="请选择有村级的行政区划"
              clearable
              @change="handleTreeVillageChange"
            />
            <div class="result">
              选中值: {{ treeVillageValue || '无' }}
            </div>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="demo-item">
            <h4>下拉选择 - 父级选择</h4>
            <p>扁平显示1-4级行政区划</p>
            <DivisionSelector
              v-model="selectParentValue"
              mode="select"
              scenario="parent"
              placeholder="请选择父级行政区划"
              clearable
              filterable
              @change="handleSelectParentChange"
            />
            <div class="result">
              选中值: {{ selectParentValue || '无' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import DivisionSelector from "@/components/DivisionSelector"

export default {
  name: "DivisionSelectorDemo",
  components: {
    DivisionSelector
  },
  data() {
    return {
      treeParentValue: null,
      cascaderValue: null,
      selectVillageValue: null,
      treeVillageValue: null,
      selectParentValue: null
    }
  },
  methods: {
    handleTreeParentChange(value, data) {
      console.log('树形父级选择:', value, data)
    },
    
    handleCascaderChange(value, path) {
      console.log('级联选择:', value, path)
    },
    
    handleSelectVillageChange(value) {
      console.log('下拉村级选择:', value)
    },
    
    handleTreeVillageChange(value, data) {
      console.log('树形村级选择:', value, data)
    },
    
    handleSelectParentChange(value) {
      console.log('下拉父级选择:', value)
    }
  }
}
</script>

<style scoped>
.demo-section {
  padding: 20px;
}

.demo-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
  background-color: #fafafa;
}

.demo-item h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
}

.demo-item p {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 13px;
}

.result {
  margin-top: 10px;
  padding: 8px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
  font-size: 13px;
  color: #409eff;
}
</style>
