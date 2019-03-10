<template>
    <div>
        <nav-bar show-back="true" title="手机注册"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 60px"></div>
            <div class="cell">
                <text style="font-size:32px;color:#000000">验证码已下发至</text>
                <text style="font-size:32px;color:#0f3691">{{tel}}</text>
            </div>

            <!-- <div class="cell">
                <text v-if="time>0" class="cell-text">{{time+'秒后重新获取'}}</text>
                <div v-else class="codeBtn" @click="getNewTelCode()">
                    <text style="color: #fff;font-size: 20px">重新获取验证码</text>
                </div>
            </div> -->
            <!-- <div class="cell2">
                <input class="input" ref="input1" type="number" @input="input1"></input>
                <input class="input" ref="input2" type="number" @input="input2"></input>
                <input class="input" ref="input3" type="number" @input="input3"></input>
                <input class="input" ref="input4" type="number" @input="input4"></input>
            </div> -->
            <div class="cell-code">
                <input class="input-code" ref="input1" type="number" placeholder="请输入验证码" v-model="input_1"></input>
                <div>
                  <text v-if="time>0" class="sendcodeBtn-gray">{{time+'秒'}}</text>
                  <text v-else class='sendcodeBtn' @click="getNewTelCode()">重新获取</text>
                </div>
            </div>
            <text class='btn' @click="onNext()">确定</text>
            <div class="cell3">
                <text style="font-size:24px;color:#969696">使用密码登陆</text>
                <text style="font-size:24px;color:#969696">收不到验证码？</text>
            </div>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import api from '@/modules/api'

var navigator = weex.requireModule('navigator')

export default {
  components: {NavBar},
  data () {
    return {
      tel: '',
      time: '',
      timer: null,
      show: true,
      code: 0,
      input_1: '',
    }
  },
  created () {
    this.countDown()
  },
  methods: {
    onNext () {
      let _code = this.input_1
      if (_code !== this.code) {
        weex.requireModule('modal').toast({
          message: '验证码不正确',
          duration: 1000
        })
        return
      }
      navigator.push({
        url: api.getUri('register3') + '?tel=' + this.tel + '&code=' + _code,
        animated: true
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
      if (self.tel && self.tel.length === 11) {
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
          self.countDown()
      }
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
        background: #969696;
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
        padding-left: 100px;
        padding-right: 100px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .cell3 {
        width: 750px;
        padding-left: 50px;
        padding-right: 50px;
        margin-top: 50px;
        margin-bottom: 20px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .cell-text {
        font-size: 28px;
        color: #969696;
    }
    .cell-code{
        width: 750px;
        margin-top: 40px;
        padding-left: 100px;
        padding-right: 100px;
        flex-direction: row;
        align-items: center;
    }
    .input-code {
        width: 360px;
        height: 80px;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        font-size: 36px;
        color: #333333;
        padding: 20px;
        maxlength: 4;
        text-align: center;
    }
  .sendcodeBtn-gray {
        width: 200px;
        height: 80px;
        border-radius: 3px;
        background-color: #333333;
        text-align: center;
        font-size: 36px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 80px;
    }
    .sendcodeBtn {
        width: 200px;
        height: 80px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 36px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
        line-height: 80px;
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
    }

    .codeBtn {
        margin-left: 30px;
        width: 150px;
        height: 50px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 14px;
        color: #ffffff;
    }
</style>
