<template>
    <div>
        <nav-bar show-back="true" :title="navTitle"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 80px"></div>
            <div class="cell">
                <image class="cell-icon" :src="isByPhone?iconPath('login_iphone'):iconPath('login_mail')"></image>
                <text class="cell-text">{{isByPhone?'手机号码':'邮箱'}}</text>
            </div>
            <div class="input-cell">
                <input class="input1" :disabled="isModify" @input="input" v-model="telOrMail"></input>
                <text style="font-size:24px;color:#0f3691" @click="sendCode">获取验证码</text>
            </div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_yzm')"></image>
                <text class="cell-text">验证码</text>
            </div>
            <input class="input2" @input="inputCode"></input>
            <text class='btn' @click="next">下一步</text>
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
      navTitle:"找回密码",
      isByPhone: false,
      telOrMail: '',
      code: '',
      codeInput: '',
      isModify:true
    }
  },
  created () {
    console.log(this.isModify)
    if(this.isModify==true||this.isModify=='true'){
      this.isModify = true
      this.isByPhone = true
      this.navTitle = "修改密码"
    }
  },
  methods: {
    inputCode (e) {
      this.codeInput = e.value
    },
    next () {
      if (this.codeInput !== this.code || this.codeInput.length === 0) {
        weex.requireModule('modal').toast({
          message: '请输入正确的验证码',
          duration: 1000
        })
        return
      }
      let url = api.getUri('password2') + (this.isByPhone ? '?tel=' : '?email=') + this.telOrMail + '&code=' + this.code + '&isModify=' + this.isModify
      navigator.push({
        url: url,
        animated: true
      })
    },
    input (e) {
      this.telOrMail = e.value
    },
    sendCode () {
      let self = this
      if (this.isByPhone) {
        if (this.telOrMail.length !== 11) {
          weex.requireModule('modal').toast({
            message: '请输入正确的手机号',
            duration: 1000
          })
          return
        }
        api.post('/edu/user/sendShortMessage',
          {phone: self.telOrMail},
          function (data) {
            weex.requireModule('modal').toast({
              message: '短信验证码已发送，请注意查收',
              duration: 1000
            })
            self.code = data.content
          },
          function (data) {
            weex.requireModule('modal').toast({
              message: data.msg,
              duration: 1000
            })
          })
      } else {
        if (this.telOrMail.length === 0) {
          weex.requireModule('modal').toast({
            message: '请输入正确的邮箱号',
            duration: 1000
          })
          return
        }
        api.post('/edu/user/sendEmail',
          {email: self.telOrMail},
          function (data) {
            weex.requireModule('modal').toast({
              message: '邮箱验证码已发送，请注意查收',
              duration: 1000
            })
            self.code = data.content
          },
          function (data) {
            weex.requireModule('modal').toast({
              message: data.msg,
              duration: 1000
            })
          })
      }
    }
  }
}
</script>

<style scoped>
    .back {
        width: 750px;
        height: 1124px;
        display: flex;
        align-items: center;
        background-color: #eef0f2;
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

    .cell-text {
        margin-left: 20px;
        font-size: 28px;
        color: #969696;
    }

    .input-cell {
        width: 650px;
        height: 90px;
        margin-top: 20px;
        margin-bottom: 30px;
        padding-left: 20px;
        padding-right: 20px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        flex-direction: row;
        align-items: center;
    }

    .input1 {
        width: 450px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        font-size: 28px;
        color: #333333;
        padding: 20px;
    }

    .input2 {
        padding: 20px;
        width: 450px;
        height: 90px;
        margin-top: 20px;
        margin-bottom: 30px;
        width: 650px;
        height: 90px;
        background-color: rgb(255, 255, 255);
    }

    .btn {
        margin-top: 50px;
        width: 650px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 36px;
        color: #ffffff;
        text-align: center;
        line-height: 90px;
    }
</style>
