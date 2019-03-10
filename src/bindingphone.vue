<template>
    <div>
        <nav-bar show-back="true" title="绑定手机"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 40px"></div>
            <div class="cell" v-if="phone">
                <text style="font-size:28px;color:#333333;margin-bottom: 40px">当前手机号：{{phone}},更换请输入</text>
            </div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_iphone')"></image>
                <text class="cell-text">手机号码</text>
            </div>
            <input class="input" type="number" placeholder="手机号码" v-model="tel"></input>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_yzm')"></image>
                <text class="cell-text">验证码</text>
            </div>
            <div class="input-cell">
                <input class="input-code" type="number" placeholder="验证码" v-model="input_1"></input>
                <div>
                    <text v-if="time>0" class="sendcodeBtn-gray">{{time+'秒'}}</text>
                    <text v-else class='sendcodeBtn' @click="getNewTelCode()">{{codetxt}}</text>
                </div>
            </div>
            <text class='btn' @click="onNext()">确定</text>
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
      phone: null,
      tel: '',
      time: '',
      timer: null,
      show: true,
      code: 0,
      input_1: '',
      codetxt: '获取验证码'
    }
  },
  methods: {
    onNext () {
      let self = this
      let _code = this.input_1
      if (_code !== this.code) {
        weex.requireModule('modal').toast({
          message: '验证码不正确',
          duration: 1000
        })
        return
      }
      api.post('/edu/user/bindingPhone',
        {
          uid: user.userId(),
          phone: self.tel,
          code: self.code
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '绑定成功',
            duration: 1000
          })
          navigator.pop()
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: data.msg,
            duration: 1000
          })
        })
    },
    countDown () {
      const TIME_COUNT = 60;
      if (!this.timer) {
        this.time = TIME_COUNT;
        this.show = false;
        this.timer = setInterval(() => {
          if (this.time > 0 && this.time <= TIME_COUNT) {
            this.time--;
          } else {
            this.show = true;
            clearInterval(this.timer);
            this.timer = null;
          }
        }, 1000)
      }
    },
    getNewTelCode () {
      let self = this
      if (this.tel && this.tel.length === 11) {
        api.post('/edu/user/sendShortMessage',
          {phone: self.tel},
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
        this.countDown()
        this.codetxt = '重新获取'
      } else {
        weex.requireModule('modal').toast({
          message: '手机号码不正确',
          duration: 1000
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
        maxlength: 11;
        text-align: center;
    }

    .btn {
        margin-top: 50px;
        width: 650px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 28px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 90px;
    }

    .input-code {
        width: 400px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        font-size: 28px;
        color: #333333;
        padding: 20px;
        maxlength: 4;
        text-align: center;
    }

    .sendcodeBtn-gray {
        width: 250px;
        height: 90px;
        border-radius: 3px;
        background-color: #333333;
        text-align: center;
        font-size: 28px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 80px;
    }

    .sendcodeBtn {
        width: 250px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 28px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 80px;
    }
</style>
