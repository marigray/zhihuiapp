<template>
    <div>
        <nav-bar title="余额明细" show-back="true"></nav-bar>
        <list class="wrapper">
            <!--<header>-->
            <!--<div class="header-view">-->
            <!--<text class="header-text">{{month}}月</text>-->
            <!--</div>-->
            <!--</header>-->
            <cell v-for="(item,index) in balanceDetailList" :key="index">
                <div class="cell-item">
                    <div class="cell-item-left">
                        <text class="title-text">{{item.operation}}</text>
                        <text class="date-text">{{timestampToTime(item.createDate)}}</text>
                    </div>
                    <text class="price-text">{{item.price}}</text>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data() {
    return {
      // month: '2018-6',
      balanceDetailList: []

    }
  },
  created () {
    this.getAccountList()
  },
  methods: {
    onNavigatorPop() {
      navigator.pop()
    },
    jumpBlanceDetail() {
      navigator.push({
        url: api.getUri('balancedetail'),
        animated: true
      })
    },
    getAccountList() {
      let self = this
      api.get('/edu/user/getBalanceDetailByPage', {uid: user.userId()},
        function (data) {
          self.balanceDetailList = data.content.records
        },
        function () {
        })
    },
    timestampToTime(timestamp) {
      var date = new Date(timestamp * 1000);
      var Y = date.getFullYear() + '-';
      var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
      var D = date.getDate() + ' ';
      var h = date.getHours() + ':';
      var m = date.getMinutes() + ':';
      var s = date.getSeconds();
      return Y + M + D + h + m + s;
    }
  }
}
</script>

<style scoped>
    .wrapper {
        flex: 1;
        background-color: rgb(249, 249, 249);
    }

    .header-view {
        width: 750px;
        height: 60px;
        background-color: #f3f2f4;
        display: flex;
        justify-content: center;
    }

    .header-text {
        font-size: 24px;
        color: #646464;
        margin-left: 15px;
    }

    .cell-item {
        width: 710px;
        height: 120px;
        margin-left: 20px;
        margin-top: 20px;
        border-radius: 10px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .cell-item-left {
        margin-left: 20px;
        height: 120px;
        width: 500px;
        justify-content: center;
    }

    .title-text {
        font-size: 24px;
        color: #333333;
        margin-bottom: 5px;
    }

    .date-text {
        font-size: 24px;
        color: #969696;
        margin-top: 5px;
    }

    .price-text {
        font-size: 28px;
        color: #ff3c2d;
        margin-right: 20px;
    }
</style>
