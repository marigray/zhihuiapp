<template>
    <div class="page" :style="{height:height+'px'}">
        <list class="list">
            <cell class="cell1" v-for="(item,index) in orderList" :key="index" @loadmore="loadMore">
                <div class="cell1-content">
                    <div class='cell1-top'>
                        <text class='cell1-teacher'>{{item.orderid}}</text>
                        <div class='cell1-gap'></div>
                        <text class='cell1-date'>{{formatDate(item.createDate/1000)}}</text>
                    </div>
                    <div class='cell1-line'></div>
                    <div class='cell1-bottom'>
                        <image class='cell1-image' mode="aspectFill" :src="item.course.image">
                        </image>
                        <div class='cell1-bottom-right'>
                            <text class='cell1-content'>{{item.course.name}}</text>
                            <div class='cell1-gap'></div>
                            <text class='cell1-type'>{{CourseTypeString(item.course.type)}}</text>
                            <div class='cell1-gap'></div>
                            <text class='cell1-price'>{{item.course.price}}</text>
                        </div>
                    </div>
                    <div class='pay-line'></div>
                    <div class='pay-container' v-if="status == 0">
                        <text class='cancel-btn' @click="cancel(item)">取消订单</text>
                        <text class='pay-btn' @click="pay(item)">去支付</text>
                    </div>
                    <div class='pay-container' v-if="status == 1">
                        <text class='pay-btn' @click="addInvoice(item)">申请发票</text>
                    </div>
                </div>
            </cell>
            <cell class="load-cell">
                <more-footer class="load-cell" :status="loadingStatus"></more-footer>
            </cell>
        </list>
        <wxc-popup :show="isInvoicePopShow" @wxcPopupOverlayClicked="hideInvoicePopShow" pos="bottom" height="400">
            <wxc-cell :has-top-border="false" :auto-accessible="false"
                      :title="selectedTitleName?selectedTitleName:'请选择发票'"
                      @wxcCellClicked="selectInvoiceTitle">
            </wxc-cell>
            <wxc-cell :has-top-border="false" :auto-accessible="false" :title="selectedTitleContent?selectedTitleContent:'请选择发票内容'"
                      @wxcCellClicked="showInvoiceContent">
            </wxc-cell>
            <wxc-button :btn-style="{'margin-top':'30px','margin-left':'24px'}" text="申请"
                        @wxcButtonClicked="apply" type="white"></wxc-button>
        </wxc-popup>
        <wxc-popup :show="isInvoiceContentShow" @wxcPopupOverlayClicked="hideInvoiceContent" pos="bottom" height="660">
            <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in ['会务费', '培训费', '咨询费', '服务费']"
                      @wxcCellClicked="setInvoiceContent(item)" :title="item">
            </wxc-cell>
        </wxc-popup>
    </div>
</template>

<script>
import api from '@/modules/api'
import user from '@/modules/user'
import PageLoadError from './components/page-load-error'
import MoreFooter from './components/more-footer'
import mixin from '@/modules/mixin'
import {WxcPopup, WxcCell, WxcButton} from 'weex-ui'

var navigator = weex.requireModule('navigator')

export default {
  components: {PageLoadError, MoreFooter, WxcPopup, WxcCell, WxcButton},
  props: ['status', 'height'],
  mixins: [mixin],
  data () {
    return {
      loadingStatus: 'loading',
      orderList: [],
      pageNo: 1,
      isInvoicePopShow: false,
      selectedOrderId: 0,
      selectedTitleId: 0,
      selectedTitleName: null,
      isInvoiceContentShow: false,
      selectedTitleContent: null
    }
  },
  created () {
    let self = this
    this.getOrderList()
    var globalEvent = weex.requireModule('globalEvent')
    globalEvent.addEventListener('pay-result', function (e) {
      let result = e.result.result
      if (result) {
        this.reset()
      }
    }.bind(this))

    globalEvent.addEventListener('invoice-selected', function (e) {
      let result = e.result
      self.selectedTitleId = result.id
      self.selectedTitleName = decodeURIComponent(result.titleName)
    })
  },
  methods: {
    showInvoiceContent () {
      this.isInvoiceContentShow = true
    },
    hideInvoiceContent () {
      this.isInvoiceContentShow = false
    },
    setInvoiceContent (item) {
      this.isInvoiceContentShow = false
      this.selectedTitleContent = item
    },
    selectInvoiceTitle () {
      navigator.push({
        url: api.getUri('invoice1') + '?isInSelectionMode=true&orderid=' + this.selectedOrderId,
        animated: true
      })
    },
    apply () {
      // console.log('xbwu')
      // console.log(this.selectedOrderId)
      // console.log(this.selectedTitleId)
      // console.log(this.selectedTitleContent)
      // console.log('xbwu')
      api.post('/edu/invoice/addInvoiceNew',
        {
          orderid: this.selectedOrderId,
          titleId: this.selectedTitleId,
          content: this.selectedTitleContent
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '发票申请成功',
            duration: 1000
          })
        }, function (data) {
          weex.requireModule('modal').toast({
            message: data.msg,
            duration: 1000
          })
        })
    },
    hideInvoicePopShow () {
      this.isInvoicePopShow = false
    },
    addInvoice (item) {
      this.selectedTitleName = null
      this.selectedTitleContent = null
      this.selectedTitleId = 0
      this.isInvoicePopShow = true
      this.selectedOrderId = item.orderid
    },
    cancel (item) {
      let self = this
      var indexOf = this.orderList.indexOf(item)
      api.post('/edu/order/deleteOrder',
        {orderid: item.orderid},
        function (data) {
          weex.requireModule('modal').toast({
            message: '订单取消成功',
            duration: 1000
          })
          if (indexOf > -1) {
            self.orderList.splice(indexOf, 1)
          }
        }, function (data) {
          weex.requireModule('modal').toast({
            message: data,
            duration: 1000
          })
        })
    },
    pay (item) {
      navigator.push({
        url: api.getUri('order') + '?orderId=' + item.orderid + '&courseId=' + item.course.id,
        animated: true
      })
    },
    reset () {
      this.loadingStatus = 'loading'
      this.pageNo = 1
      this.orderList = []
      this.getOrderList()
    },
    getOrderList () {
      let self = this
      api.get('/edu/order/getOrderList',
        {
          userid: user.userId(),
          status: this.status,
          pageNo: this.pageNo,
          pageCount: 10
        },
        function (data) {
          let array = data.content.records
          for (let i = 0; i < array.length; i++) {
            let item = array[i]
            item.course = item.courseList[0]
          }
          self.orderList = self.orderList.concat(array)
          if (self.orderList.length === 0) {
            self.loadingStatus = 'no-data'
          } else {
            if (data.content.total === self.orderList.length) {
              self.loadingStatus = 'no-more'
            }
          }
        },
        function (e) {
          self.loadingStatus = 'error'
        }
      )
    },
    loadMore () {
      if (this.loadingStatus === 'no-more' || this.loadingStatus === 'no-data') return
      this.pageNo++
      this.getOrderList()
    }
  }
}
</script>

<style scoped>

    .load-cell {
        height: 150px;
        width: 750px;
    }

    .page {
        width: 750px;
    }

    .list {
        width: 750px;
        background-color: rgb(249, 249, 249);
    }

    .tag-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .tag-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tag-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tag-text-selected {
        color: #0f3691;
    }

    .tag-line {
        margin-top: 20px;
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tag-line-selected {
        opacity: 1;
    }

    /* cell */

    .cell1 {
        padding: 20px;
        background-color: rgb(249, 249, 249);
        border-radius: 1px;
    }

    .cell1-content {
        background-color: white;
        border-radius: 10px;
    }

    .cell1-top {
        height: 80px;
    }

    .cell1-line {
        height: 1px;
        background-color: lightgray;
    }

    .cell1-top {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .cell1-teacher {
        margin-left: 20px;
        font-size: 28px;
        color: #333;
    }

    .cell1-gap {
        flex: 1;
    }

    .cell1-date {
        font-size: 28px;
        color: #333;
        margin-right: 20px;
    }

    .cell1-bottom {
        display: flex;
        height: 220px;
        flex-direction: row;
    }

    .cell1-bottom-right {
        display: flex;
        margin-top: 20px;
        margin-bottom: 20px;
        flex: 1;
        flex-direction: column;
    }

    .cell1-image {
        margin: 20px;
        width: 250px;
        height: 180px;
        border-radius: 10px;
    }

    .cell1-content {
        font-size: 24px;
    }

    .cell1-type {
        display: block;
        padding: 5px;
        font-size: 20px;
        color: white;
        width: 60px;
        text-align: center;
        border-radius: 5px;
        background-color: #0f3691;
    }

    .cell1-price {
        font-size: 28px;
        color: #ec5f59;
    }

    .pay-container {
        display: flex;
        flex-direction: row;
        height: 80px;
        align-items: center;
        justify-content: flex-end;
    }

    .pay-line {
        width: 710px;
        height: 1px;
        background-color: #f0f0f0;
    }

    .cancel-btn,
    .pay-btn {
        font-size: 24px;
        margin-right: 20px;
        border-style: solid;
        border-width: 1px;
        border-color: rgb(150, 150, 150);
        width: 140px;
        text-align: center;
        border-radius: 30px;
        padding-top: 10px;
        padding-bottom: 10px;
    }

    .pay-btn {
        color: #ec5f59;
        border-color: #ec5f59;
    }

</style>
