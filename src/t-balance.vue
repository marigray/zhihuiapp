<template>
    <div>
        <nav-bar title="账户余额" show-back="true">
            <text slot="right-icon1" class="coures-add" @click="jumpBlanceDetail">余额明细</text>
        </nav-bar>
        <div class="wrapper">
            <div class="account-div">
                <text class="tabText">我的余额：</text>
                <div class="number-div">
                    <text class="symbol-text">¥  </text>
                    <text class="account-text">{{balance ? balance : 0}}</text>
                </div>
            </div>
            <div class="bankaccount-div" @click="jumpBankAccount()">
                <text class="bankaccount-left-text">银行账户</text>
                <text class="bankaccount-right-text">请添加您的收款帐号 ></text>
            </div>
            <div class="input-div">
                <input placeholder="请输入您要提现的金额" class="input" type="number" @input="withdrawInput"></input>
            </div>
            <div class="btn" @click="withdraw">
                <text class="btn-text">我要提现</text>
            </div>
            <text class="tips">提现资金将在1个工作日到帐，请注意查收</text>
        </div>
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
  data () {
    return {
      balance: 0,
      withdrawNumber: 0
    }
  },
  created () {
    this.getBalance()
  },
  methods: {
    withdrawInput (e) {
      this.withdrawNumber = e.value
    },
    getBalance () {
      let self = this
      api.get('/edu/user/getUserDetail', {id: user.userId()},
        function (data) {
          self.balance = data.content.userInfo.balance
        },
        function (e) {
        })
    },
    onNavigatorPop () {
      navigator.pop()
    },
    jumpBlanceDetail () {
      navigator.push({
        url: api.getUri('balancedetail'),
        animated: true
      })
    },
    jumpBankAccount () {
      navigator.push({
        url: api.getUri('bankaccount'),
        animated: true
      })
    },
    withdraw () {
      if (this.withdrawNumber > this.balance) {
        weex.requireModule('modal').toast({
          message: '请输入正确的提现金额',
          duration: 1000
        })
        return
      }
      api.post('/edu/user/cashApply', {uid: user.userId(), price: this.withdrawNumber},
        function (data) {
          weex.requireModule('modal').toast({
            message: '申请成功，资金将在一个工作日到账',
            duration: 1000
          })
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1000
          })
        })
    }
  }
}
</script>

<style scoped>
    .coures-add {
        width: 140px;
        height: 50px;
        position: absolute;
        right: 20px;
        top: 25px;
        line-height: 50px;
        text-align: right;
        color: white;
    }

    .wrapper {
        flex: 1;
        background-color: rgb(249, 249, 249);
        align-items: center;
    }

    .account-div {
        width: 710px;
        height: 300px;
        border-radius: 10px;
        background-color: rgb(255, 255, 255);
        margin-top: 20px;
    }

    .tabText {
        font-size: 28px;
        color: #646464;
        margin-top: 20px;
        margin-left: 20px;
    }

    .number-div {
        width: 710px;
        margin-top: 50px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .symbol-text {
        font-size: 30px;
        color: #ff3c2d;
        margin-top: 10px;
    }

    .account-text {
        font-size: 60px;
        color: #ff3c2d;
    }

    .bankaccount-div {
        width: 710px;
        height: 90px;
        margin-top: 20px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .bankaccount-left-text {
        font-size: 24px;
        color: #333333;
        margin-left: 20px;
    }

    .bankaccount-right-text {
        font-size: 24px;
        color: #0f3691;
        margin-right: 20px;
    }

    .input-div {
        width: 710px;
        height: 90px;
        margin-top: 100px;
        border-radius: 5px;
        border-style: solid;
        border-width: 2px;
        border-color: rgb(15, 54, 145);
        background-color: rgb(255, 255, 255);
        justify-content: center;
        align-items: center;
    }

    .input {
        font-size: 28px;
        color: #0f3691;
        margin-left: 20px;
    }

    .btn {
        width: 710px;
        height: 90px;
        border-radius: 5px;
        margin-top: 20px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
    }

    .btn-text {
        font-size: 28px;
        color: #ffffff;
    }

    .tips {
        font-size: 24px;
        color: #646464;
        margin-top: 20px;
    }
</style>
