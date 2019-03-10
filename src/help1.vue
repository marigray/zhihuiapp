<template>
    <div class="wrapper">
        <nav-bar show-back="true" title="帮助与反馈"></nav-bar>
        <div class="cell" @click="feedback(1)">
            <text class="left-txt">关于取消课程与变更策略</text>
            <image class="arrow" :src="iconPath('icon_right_black')"></image>
        </div>
        <div class="cell" @click="feedback(2)">
            <text class="left-txt">关于评分系统说明</text>
            <image class="arrow" :src="iconPath('icon_right_black')"></image>
        </div>
        <div class="cell" @click="feedback(3)">
            <text class="left-txt">关于投诉争议的说明</text>
            <image class="arrow" :src="iconPath('icon_right_black')"></image>
        </div>
        <div v-if="!isClientStudent()" class="cell" @click="feedback(4)">
            <text class="left-txt">关于讲师代开发票政策</text>
            <image class="arrow" :src="iconPath('icon_right_black')"></image>
        </div>
        <div v-if="!isClientStudent()" class="cell" @click="feedback(5)">
            <text class="left-txt">关于余额提现的说明</text>
            <image class="arrow" :src="iconPath('icon_right_black')"></image>
        </div>
        <textarea class="textarea" placeholder="请输入反馈内容" @input="textInput"></textarea>
        <div class="btn" @click="submit">
            <text style="font-size: 36px;color: #ffffff;">提交</text>
        </div>
        <div class="bottom">
            <text style="font-size:24px;color:#0f3169;margin-bottom:5px" >官网 www.zhihui-app.com</text>
            <text style="font-size:24px;color:#0f3169;margin-bottom:5px">客服电话： 0571-28874128-6</text>
            <text style="font-size:24px;color:#0f3169;margin-bottom:20px">客服邮箱： cs@xiaotongtech.net</text>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import user from '@/modules/user'
import api from '@/modules/api'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      content: null
    }
  },
  methods: {
    isClientStudent: function isClientStudent() {
      return weex.config.env.scheme === 'zhihui';
    },
    textInput (e) {
      this.content = e.value
    },
    submit () {
      api.post('/edu/message/addFeedback', {uid: user.userId(), content: this.content},
        function (data) {
          weex.requireModule('modal').toast({
            message: '感谢您的宝贵意见，我们会尽快回复',
            duration: 1000
          })
        },
        function () {
          weex.requireModule('modal').toast({
            message: '提交失败，请稍后再试',
            duration: 1000
          })
        })
    },
    feedback (type) {
      navigator.push({
        url: api.getUri('feedback') + '?type=' + type,
        animated: true
      })
    }
  }
}
</script>

<style scoped>
    .wrapper {
        background-color: rgb(244, 244, 244);
    }

    .cell {
        width: 710px;
        height: 100px;
        margin-top: 30px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        background-color: #fff;
    }

    .left-txt {
        margin-left: 20px;
        font-size: 24px;
        color: #333333;
    }

    .arrow {
        margin-right: 20px;
        width: 30px;
        height: 30px;
    }

    .textarea {
        font-size: 26px;
        lines:5;
        width: 710px;
        height: 200px;
        margin-top: 30px;
        margin-left: 20px;
        padding-top: 20px;
        padding-bottom: 20px;
        padding-left: 20px;
        padding-right: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        background-color: #fff;
    }

    .btn {
        margin-top: 50px;
        margin-left: 20px;
        width: 710px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 36px;
        color: #ffffff;
    }

    .bottom {
        align-items: center;
        justify-content: flex-end;
        flex: 1;
    }
</style>
