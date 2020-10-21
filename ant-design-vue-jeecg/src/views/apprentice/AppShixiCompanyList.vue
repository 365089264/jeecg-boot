<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="企业名称">
              <a-input placeholder="请输入企业名称" v-model="queryParam.companyName"></a-input>
            </a-form-item>
          </a-col>
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
      <a-button @click="handleAdd" type="primary" icon="plus" >新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('实习公司')">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel" v-has="'company:delete'"><a-icon type="delete" />删除</a-menu-item>
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

        <span slot="action" slot-scope="text, record">
          <a v-if="record.status==0" @click="handleEdit(record)" >编辑</a>
          <a-divider type="vertical" />
          <a @click="handleDetail(record)" >详情</a>
          <a-divider type="vertical" />
          <a-popconfirm v-if="record.status==0" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a-popconfirm v-if="record.status==0" v-has="'company:approve1'" title="确定审批吗?" @confirm="() => handleApprove(record.id,1)">
            <a>审批</a>
          </a-popconfirm>
          <a-popconfirm  v-if="record.status==1" v-has="'company:approve2'" title="确定批准吗?" @confirm="() => handleApprove(record.id,2)">
            <a>批准</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    <app-shixi-company-modal ref="modalForm" @ok="modalFormOk"></app-shixi-company-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {putAction} from '@/api/manage';
  import {approveCompany} from '@/api/api'
  import AppShixiCompanyModal from './modules/AppShixiCompanyModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'AppShixiCompanyList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      AppShixiCompanyModal
    },
    data () {
      return {
        description: '实习公司管理页面',
        queryParam: {companyName:this.$route.query.companyName},
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
            title:'企业名称',
            align:"center",
            dataIndex: 'companyName'
          },
          {
            title:'企业类型',
            align:"center",
            dataIndex: 'companyType'
          },
          {
            title:'统一社会信用代码',
            align:"center",
            dataIndex: 'companyCode'
          },
          {
            title:'法定代表人',
            align:"center",
            dataIndex: 'companyUser'
          },
          {
            title:'成立日期',
            align:"center",
            dataIndex: 'startdate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'企业地址',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'经营范围',
            align:"center",
            dataIndex: 'businessScope'
          },
          {
            title:'营业执照',
            align:"center",
            dataIndex: 'license',
            scopedSlots: {customRender: 'fileSlot'}
          },
           {
            title:'审批状态',
            align:"center",
            dataIndex: 'status_dictText'
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
          list: "/apprentice/appShixiCompany/list",
          delete: "/apprentice/appShixiCompany/delete",
          deleteBatch: "/apprentice/appShixiCompany/deleteBatch",
          exportXlsUrl: "/apprentice/appShixiCompany/exportXls",
          importExcelUrl: "apprentice/appShixiCompany/importExcel",
          
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
      },
      handleApprove: function (id, status, username) {
        let that = this;
        approveCompany({ids: id, status: status}).then((res) => {
          if (res.success) {
            that.$message.success(res.message);
            that.loadData();
          } else {
            that.$message.warning(res.message);
          }
        });
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>