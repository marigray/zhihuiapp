<template>
    <div>
        <nav-bar show-back="true" title="绑定邮箱"></nav-bar>
        <div class="back">
            <div style="margin-bottom: 80px"></div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_mail')"></image>
                <text class="cell-text">请填写邮箱地址</text>
            </div>
            <div class="input-cell">
                <input class="input"></input>
                <text style="font-size:24px;color:#0f3691">获取验证码</text>
            </div>
            <div class="cell">
                <image class="cell-icon" :src="iconPath('login_yzm')"></image>
                <text class="cell-text">验证码</text>
            </div>
            <div class="input-cell">
                <input class="input"></input>
            </div>
            <text class='btn'>确定</text>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {}
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
        justify-content: space-between;
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

