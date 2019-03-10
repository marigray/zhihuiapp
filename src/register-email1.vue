<template>
    <div>
        <nav-bar show-back="true" title="邮箱注册"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 60px"></div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('zh_mail_gery')"></image>
                <text class="cell-text">请填写邮箱地址</text>
            </div>
            <div class="input-cell">
                <input class="input" @input="emailInput"></input>
            </div>
            <text class='btn' @click="next()">下一步</text>
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
      email: null
    }
  },
  methods: {
    emailInput (e) {
      this.email = e.value
    },
    next () {
      let self = this
      if (this.email) {
        api.post('/edu/user/sendEmail',
          {email: self.email},
          function (data) {
            weex.requireModule('modal').toast({
              message: '验证码已发送到您的邮箱，请注意查收',
              duration: 1000
            })
            navigator.push({
              url: api.getUri('register-email2') + '?email=' + self.email + '&code=' + data.content,
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
        message: '请输入正确的邮箱',
        duration: 1000
      })
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

    .cell-icon {
        width: 60px;
        height: 60px;
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
        margin-left: 10px;
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
        justify-content: space-between;
    }

    .btn {
        margin-top: 50px;
        width: 650px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 36px;
        color: white;
        justify-content: center;
        align-items: center;
        line-height: 90px;
    }

    .input {
        width: 450px;
        height: 80px;
        margin-top: 20px;
        margin-bottom: 30px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        font-size: 28px;
        color: #333333;
    }
</style>
