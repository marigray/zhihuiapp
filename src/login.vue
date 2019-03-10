<template>
    <div>
        <nav-bar show-back="true" title="智荟"></nav-bar>
        <div class="back">
            <div>
                <image class='logo-img' :src="iconPath('logo')"></image>
            </div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_iphone')"></image>
                <text class="cell-text">手机号码 / 邮箱</text>
            </div>
            <input class="input" :value="account" @input="accountInput"></input>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_password')"></image>
                <text class="cell-text">密码</text>
            </div>
            <input type="password" class="input" @input="passwordInput" :value="password"></input>
            <div class="cell2">
                <text style="font-size:24px;color:#969696" @click="onNavigator('register1')">新用户注册</text>
                <text style="font-size:24px;color:#969696" @click="forget">忘记密码</text>
            </div>
            <div @click="login">
                <text class="btn">登录</text>
            </div>
            <div class="cell2" v-if="isClientStudent()">
                <div class="line"></div>
                <text style="font-size:24px;color:#969696">或使用</text>
                <div class="line"></div>
            </div>
            <div class='btn-wx' @click="weChatLogin" v-if="isClientStudent()">
                <image class="wx-icon" :src="iconPath('zh_wechat')"></image>
                <text class="wx-text">微信登录</text>
            </div>
        </div>
        <wxc-loading class="page-loading" :show="isLogining" loading-text="登录中"></wxc-loading>
        <wxc-popup :show="isForgetPopShow" @wxcPopupOverlayClicked="hideForgetPop" pos="bottom" height="300">
            <wxc-cell :has-top-border="false" :auto-accessible="false"
                      title="通过邮件" @wxcCellClicked="findPdByEmail">
            </wxc-cell>
            <wxc-cell :has-top-border="false" :auto-accessible="false"
                      title="通过手机" @wxcCellClicked="findPdByPhone">
            </wxc-cell>
            <wxc-button :btn-style="{'margin-top':'30px','margin-left':'24px'}" text="取消"
                        @wxcButtonClicked="hideForgetPop" type="white"></wxc-button>
        </wxc-popup>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'
import mixin from '@/modules/mixin'
import {WxcLoading, WxcPopup, WxcCell} from 'weex-ui'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, WxcLoading, WxcPopup, WxcCell},
  data () {
    return {
      account: '',
      password: '',
      isLogining: false,
      isForgetPopShow: false
    }
  },
  methods: {
    accountInput (e) {
      this.account = e.value
    },
    passwordInput (e) {
      this.password = e.value
    },
    onNavigator (link) {
      navigator.push({
        url: api.getUri(link),
        animated: true
      })
    },
    findPdByEmail () {
      navigator.push({
        url: api.getUri('password1'),
        animated: true
      })
    },
    findPdByPhone () {
      navigator.push({
        url: api.getUri('password1') + '?isByPhone=true',
        animated: true
      })
    },
    forget () {
      this.isForgetPopShow = true
    },
    hideForgetPop () {
      this.isForgetPopShow = false
    },
    login () {
      if (this.password.length === 0 || this.account.length === 0) {
        weex.requireModule('modal').toast({
          message: '请输入正确的账号或者密码',
          duration: 1
        })
        return
      }
      let self = this
      this.isLogining = true
      weex.requireModule('user').getJPushAlias(function (uuid) {
        // weex.requireModule('modal').toast({
        //   message: uuid,
        //   duration: 1
        // })
        api.post('/edu/user/login',
          {
            account: self.account,
            password: self.password,
            platform: self.isClientStudent() ? 1 : 2,
            alias: (uuid !== null ? uuid : '')
          },
          function (data) {
            self.isLogining = false
            weex.requireModule('modal').toast({
              message: '登录成功',
              duration: 1
            })
            weex.requireModule('user').saveUserid({userid: data.content.userInfo.id})
            weex.requireModule('myevent').sendGlobal('user-changed', {})
            navigator.pop()
          }, function (data) {
            self.isLogining = false
            weex.requireModule('modal').toast({
              message: data.msg,
              duration: 1
            })
          })
      })
    },
    weChatLogin () {
      weex.requireModule('user').weChatLogin()
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
        margin-bottom: 100px;
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
        font-size: 36px;
        color: #333333;
        padding: 20px;
    }

    .btn {
        width: 650px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 36px;
        color: white;
        text-align: center;
        line-height: 90px;
    }

    .line {
        height: 2px;
        width: 250px;
        background-color: #dde2e6;
    }

    .wx-icon {
        width: 40px;
        height: 40px;
    }

    .btn-wx {
        width: 650px;
        height: 90px;
        border-style: solid;
        border-width: 2px;
        border-color: rgb(111, 180, 82);
        border-radius: 3px;
        background-color: rgb(238, 240, 242);
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .wx-text {
        font-size: 32px;
        color: #7ebb66;
        text-align: center;
        width: 200px;
    }
</style>
