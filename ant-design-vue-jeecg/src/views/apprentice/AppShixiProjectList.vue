<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="项目名称">
              <a-input placeholder="请输入项目名称" v-model="queryParam.projectName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="实习企业">
              <j-popup placeholder="请选择实习企业" v-model="queryParam.companyName" code="shixi_company" org-fields="company_name" dest-fields="company_name" :field="getPopupField('company_name')"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="企业导师">
                <a-input placeholder="请输入企业导师" v-model="queryParam.companyMentor"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="学校导师">
                <j-popup placeholder="请选择学校导师" v-model="queryParam.schoolMentor" code="daoshi" org-fields="school_mentor" dest-fields="school_mentor" :field="getPopupField('school_mentor')"/>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :lg="7" :md="8" :sm="24">
              <a-form-item label="实习组长">
                <j-popup placeholder="请选择实习组长" v-model="queryParam.leader" code="banzhuren" org-fields="leader" dest-fields="leader" :field="getPopupField('leader')"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('实习项目')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>
         <template slot="companySlot" slot-scope="text">
          <router-link :to="{ path: '/apprentice/appShixiCompanyList?companyName='+text }">
            {{ text }}
          </router-link>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
               <a-menu-item>
                <a @click="handleDetail(record)">学生自评</a>
              </a-menu-item>
                  <a-menu-item>
                <a @click="handleDetail(record)">组内互评</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleDetail(record)">学校导师点评</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleDetail(record)">企业导师点评</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleDetail(record)">业主评价</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <app-shixi-project-modal ref="modalForm" @ok="modalFormOk"></app-shixi-project-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import AppShixiProjectModal from './modules/AppShixiProjectModal'

  export default {
    name: 'AppShixiProjectList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      AppShixiProjectModal
    },
    data () {
      return {
        description: '实习项目管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'项目名称',
            align:"center",
            dataIndex: 'projectName'
          },
          {
            title:'实习企业',
            align:"center",
            dataIndex: 'companyName',
            scopedSlots: {customRender: 'companySlot'}
          },
          {
            title:'开始时间',
            align:"center",
            dataIndex: 'startdate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'结束时间',
            align:"center",
            dataIndex: 'enddate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'企业导师',
            align:"center",
            dataIndex: 'companyMentor'
          },
          {
            title:'学校导师',
            align:"center",
            dataIndex: 'schoolMentor'
          },
          {
            title:'实习组长',
            align:"center",
            dataIndex: 'leader'
          },
          {
            title:'创建用户',
            align:"center",
            dataIndex: 'createBy_dictText'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/apprentice/appShixiProject/list",
          delete: "/apprentice/appShixiProject/delete",
          deleteBatch: "/apprentice/appShixiProject/deleteBatch",
          exportXlsUrl: "/apprentice/appShixiProject/exportXls",
          importExcelUrl: "apprentice/appShixiProject/importExcel",
          
        },
        dictOptions:{},
      }
    },
    created() {
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>