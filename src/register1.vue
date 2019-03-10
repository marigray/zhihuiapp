<template>
    <div>
        <nav-bar show-back="true" title="注册"></nav-bar>
        <div class="back">
            <div>
                <image class='logo-img' :src="iconPath('logo')"></image>
            </div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_iphone')"></image>
                <text class="cell-text">请输入手机号</text>
            </div>
            <input class="input" @input="phoneInput"></input>
            <div class="cell">
                <image :src="isChecked?iconPath('login_check'):iconPath('login_box')" class="checkbox" @click="onCheck"></image>
                <text style="font-size:24px;color:#969696;margin-left:20px">我已阅读并同意《</text>
                <text style="font-size:24px;color:#0f3691" @click="onAgreement">智荟服务条款</text>
                <text style="font-size:24px;color:#969696">》</text>
            </div>
            <text class='btn' @click="onNext">下一步</text>
            <div class="cell2">
                <div class="line"></div>
                <text style="font-size:24px;color:#969696">使用其他方式注册</text>
                <div class="line"></div>
            </div>
            <div class='btn-mail' @click="onRegisterEmail">
                <image class="mail-icon" :src="iconPath('login_mail_big')"></image>
                <text class="mail-text">邮箱注册</text>
            </div>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      isChecked: true,
      tel: null
    }
  },
  methods: {
    onCheck () {
      this.isChecked = !this.isChecked
    },
    onAgreement () {
      navigator.push({
        url: api.getUri('login-eula'),
        animated: true
      })
    },
    onRegisterEmail () {
      navigator.push({
        url: api.getUri('register-email1'),
        animated: true
      })
    },
    onNext () {
      if (!this.isChecked) {
        weex.requireModule('modal').toast({
          message: '请先同意服务条款',
          duration: 1000
        })
        return
      }
      let self = this
      if (this.tel && this.tel.length === 11) {
        api.post('/edu/user/sendShortMessage',
          {phone: self.tel},
          function (data) {
            weex.requireModule('modal').toast({
              message: '短信验证码已发送，请注意查收',
              duration: 1000
            })
            navigator.push({
              url: api.getUri('register2') + '?tel=' + self.tel + '&code=' + data.content,
              animated: true
            })
          },
          function (data) {
            weex.requireModule('modal').toast({
              message: data.msg,
              duration: 1000
            })
          })
        return
      }
      weex.requireModule('modal').toast({
        message: '请输入正确的手机号',
        duration: 1000
      })
    },
    phoneInput (e) {
      this.tel = e.value
    }
  }
}
</script>

<style scoped>
    .back {
        flex: 1;
        display: flex;
        align-items: center;
        background-color: #eef0f2;
    }

    .logo-img {
        width: 200px;
        height: 200px;
        background-color: #eef0f2;
        border-radius: 100px;
        margin-top: 100px;
        margin-bottom: 280px;
        border-width: 1px;
        border-color: rgb(200, 200, 200);
        border-style: solid;
    }

    .cell-icon {
        width: 40px;
        height: 40px;
    }

    .cell {
        width: 750px;
        padding-left: 50px;
        padding-right: 50px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .cell2 {
        width: 750px;
        padding-left: 50px;
        padding-right: 50px;
        margin-top: 20px;
        margin-bottom: 20px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .cell-text {
        margin-left: 20px;
        font-size: 28px;
        color: #969696;
    }

    .input {
        width: 650px;
        height: 90px;
        margin-top: 20px;
        margin-bottom: 30px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        font-size: 28px;
        color: #333333;
        padding: 20px;
    }

    .btn {
        margin-top: 20px;
        width: 650px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 36px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 90px;
    }

    .line {
        height: 2px;
        width: 220px;
        background-color: #dde2e6;
    }

    .mail-icon {
        width: 40px;
        height: 40px;
    }

    .btn-mail {
        width: 650px;
        height: 90px;
        border-style: solid;
        border-width: 2px;
        border-color: rgb(15, 54, 145);
        border-radius: 3px;
        background-color: rgb(238, 240, 242);
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .mail-text {
        font-size: 32px;
        color: #0f3691;
        margin-left: 20px;
    }

    .checkbox {
        width: 50px;
        height: 50px;
    }
</style>
