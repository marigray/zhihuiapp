<template>
    <list class="list">
        <cell class='cell1' v-for="(item,index) in orderList" :key="index" @loadmore="loadMore">
            <div class="cell1-content">
                <div class='cell1-top'>
                    <text class='cell1-buyer'>购买人：{{item.userName}}</text>
                    <div class='cell1-gap'></div>
                    <text class='cell1-date'>订单时间：{{formatDate(item.createDate/1000)}}</text>
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
                <div class='pay-container'>
                    <text class='cancel-btn'>取消订单</text>
                </div>
            </div>
        </cell>
        <cell class="load-cell">
            <more-footer :status="loadingStatus"></more-footer>
        </cell>
    </list>
</template>

<script>
import api from '@/modules/api'
import user from '@/modules/user'
import PageLoadError from './components/page-load-error'
import MoreFooter from './components/more-footer'
import mixin from '@/modules/mixin'

export default {
  components: {PageLoadError, MoreFooter},
  props: ['status'],
  mixins: [mixin],
  data () {
    return {
      loadingStatus: 'loading',
      orderList: [],
      pageNo: 1
    }
  },
  created () {
    this.getOrderList()
  },
  methods: {
    getOrderList () {
      let self = this
      api.get('/edu/order/getOrderListByTeacher',
        {
          teacherid: user.userId(),
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
        function () {
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
    .list {
        background-color: rgb(249, 249, 249);
        flex: 1;
    }

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

    .cell1-buyer {
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
</style>
