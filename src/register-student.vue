<template>
    <div>
        <nav-bar show-back="true" title="注册"></nav-bar>
        <div class="back">
            <div class="cell">
            </div>
            <div class="input-cell">
                <text style="font-size:24px;color:#333333">姓名</text>
                <div class="input-cell2">
                    <input :value="name"></input>
                    <!--<image class="img-icon" src="file:///android_asset/images/content/zh_mail_gery.png"></image>-->
                    <image class="img-icon" :src="iconPath('icon_pen')"></image>
                </div>
            </div>
            <div class='btn'>完成</div>
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
    return {
      name: '王大锤'
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

    .cell {
        width: 750px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .input-cell {
        width: 690px;
        height: 90px;
        margin-top: 100px;
        padding-left: 20px;
        padding-right: 20px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: solid;
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        opacity: 0.502;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
    }

    .input-cell2 {
        height: 90px;
        width: 500px;
        background-color: rgb(255, 255, 255);
        opacity: 0.502;
        flex-direction: row;
        align-items: center;
        justify-content: flex-end;
        font-size: 24px;
        color: #0f3691;
    }

    .input {
        font-size: 24px;
        color: #0f3691;
    }

    .btn {
        margin-top: 80px;
        width: 690px;
        height: 90px;
        border-radius: 3px;
        background-color: rgb(15, 54, 145);
        text-align: center;
        font-size: 36px;
        color: #ffffff;
        justify-content: center;
        align-items: center;
    }

    .img-icon {
        width: 40px;
        height: 40px;
        /*background: #969696;*/
        /*margin-left: 20px;*/
    }
</style>
