<template>
    <div>
        <nav-bar show-back="true" title="手机注册"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 60px"></div>
            <div class="cell">
                <text style="font-size:32px;color:#000000">请为您的帐号</text>
                <text style="font-size:32px;color:#0f3691">{{tel}}</text>
                <text style="font-size:32px;color:#000000"> 设置一个新密码</text>
            </div>
            <div style="margin-bottom: 60px"></div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_password')"></image>
                <text class="cell-text">密码</text>
            </div>
            <input type="password" class="input" @input="onPasswordInput1"></input>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_password')"></image>
                <text class="cell-text">重复密码</text>
            </div>
            <input type="password" class="input" @input="onPasswordInput2"></input>
            <text class='btn' @click="confirm()">保存密码</text>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      tel: '13812345678',
      password1: null,
      password2: null,
      code: ''
    }
  },
  methods: {
    onPasswordInput1 (e) {
      this.password1 = e.value
    },
    onPasswordInput2 (e) {
      this.password2 = e.value
    },
    confirm () {
      if (this.password1 !== this.password2) {
        weex.requireModule('modal').toast({
          message: '二次密码输入不一致',
          duration: 1000
        })
        return
      }
      let self = this
      api.post('/edu/user/signIn',
        {
          phone: this.tel,
          password: this.password1,
          roleCode: this.isClientStudent() ? 'student' : 'teacher',
          code: this.code
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '注册成功',
            duration: 1000
          })
          weex.requireModule('user').saveUserid({userid: data.content})
          weex.requireModule('user').exitWXPages()
          weex.requireModule('myevent').sendGlobal('user-changed', {})
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: data,
            duration: 1000
          })
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
        width: 40px;
        height: 40px;
        /*background: #969696;*/
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

    .input {
        width: 650px;
        height: 90px;
        margin-top: 20px;
        margin-bottom: 30px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        font-size: 36px;
        color: #333333;
        padding: 20px;
    }

    .btn {
        margin-top: 50px;
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
        text-align: center;
    }

</style>