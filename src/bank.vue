<template>
    <div class="bg">
        <nav-bar show-back="true" title="银行转帐信息"></nav-bar>
        <div class="cell">
            <text class="left-txt">收款银行</text>
            <input class="right-txt" value="上海农商银行徐汇支行"></input>
        </div>
        <div class="cell">
            <text class="left-txt">收款帐号</text>
            <input class="right-txt" value="6228 0015 9906 5788"></input>
        </div>
        <div class="cell">
            <text class="left-txt">开户名称</text>
            <input class="right-txt" value="上海智荟科技有限公司"></input>
        </div>
        <div class="btn">
            <text style="font-size: 36px;color: #ffffff;">完成</text>
        </div>
        <div class="bottom">
            <text style="font-size:24px;color:#646464;margin-bottom:30px">如遇支付问题，请拨打客服热线</text>
            <text style="font-size:24px;color:#0f3169;margin-bottom:30px">100-800-1258</text>
        </div>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'

export default {
  components: {NavBar},
  data () {
    return {}
  }
}
</script>

<style scoped>
    .bg {
        background-color: #eef0f2;
    }

    .cell {
        width: 710px;
        height: 100px;
        margin-top: 30px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
        justify-content: space-between;
        font-size: 24px;
        background-color: white;
        border-radius: 5px;
    }

    .left-txt {
        margin-left: 20px;
        font-size: 24px;
        color: #333333;
        width: 100px;
    }

    .right-txt {
        margin-right: 20px;
        font-size: 24px;
        color: #0f3169;
        width: 300px;
    }

    .arrow {
        margin-right: 20px;
        width: 20px;
        height: 20px;
    }

    .textarea {
        font-size: 50px;
        width: 690px;
        margin-top: 30px;
        margin-left: 30px;
        padding-top: 20px;
        padding-bottom: 20px;
        padding-left: 20px;
        padding-right: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .btn {
        margin-top: 60px;
        margin-left: 20px;
        width: 710px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 28px;
        color: #ffffff;
    }

    .bottom {
        flex-direction: row;
        align-items: flex-end;
        justify-content: center;
        flex: 1;
    }
</style>
