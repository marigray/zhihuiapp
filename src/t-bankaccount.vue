<template>
    <div>
        <nav-bar title="银行账户" show-back="true"></nav-bar>
        <div class="wrapper">
            <div class="bankaccount-div">
                <text class="bankaccount-left-text">真实姓名</text>
                <input placeholder="请输入您的真实姓名" class="input" v-model="realName"></input>
                <image class="pen-image" :src="iconPath('icon_pen')"></image>
            </div>
            <div class="bankaccount-div">
                <text class="bankaccount-left-text">开户银行</text>
                <input placeholder="请输入您的开户银行" class="input" v-model="bank"></input>
                <image class="pen-image" :src="iconPath('icon_pen')"></image>
            </div>
            <div class="bankaccount-div">
                <text class="bankaccount-left-text">银行账号</text>
                <input placeholder="请输入您的银行账号" class="input" v-model="bankAccount"></input>
                <image class="pen-image" :src="iconPath('icon_pen')"></image>
            </div>
            <div class="btn" @click="update">
                <text class="btn-text">完成</text>
            </div>
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
      bank: '',
      bankAccount: '',
      realName: '',
      id: 0
    }
  },
  created () {
    this.getInfo()
  },
  methods: {
    onNavigatorPop () {
      navigator.pop()
    },
    getInfo () {
      let self = this
      api.get('/edu/user/getUserBankInfo', {uid: user.userId()},
        function (data) {
          self.bank = data.content.bank ? data.content.bank : ''
          self.bankAccount = data.content.bankAccount ? data.content.bankAccount : ''
          self.realName = data.content.realName ? data.content.realName : ''
          self.id = data.content.id
        },
        function () {
        })
    },
    update () {
      api.post('/edu/user/addOrUpdateBank',
        {
          uid: user.userId(),
          bank: this.bank,
          bankAccount: this.bankAccount,
          realName: this.realName,
          id: this.id
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '更新成功',
            duration: 1000
          })
          navigator.pop()
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
    .wrapper {
        flex: 1;
        background-color: rgb(249, 249, 249);
        align-items: center;
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

    .input {
        font-size: 24px;
        height: 40px;
        line-height: 40px;
        color: #0f3691;
        margin-right: 50px;
        flex: 1;
        text-align: right;
    }

    .pen-image {
        width: 30px;
        height: 30px;
        margin-right: 20px;
    }

    .btn {
        width: 710px;
        height: 90px;
        border-radius: 5px;
        margin-top: 60px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
    }

    .btn-text {
        font-size: 28px;
        color: #ffffff;
    }
</style>
