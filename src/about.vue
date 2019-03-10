<template>
    <div>
        <nav-bar show-back="true" title="关于我们"></nav-bar>

        <div class="section">
            <div class="logo-line">
                <image class="logo" :src="iconPath('logo')"></image>
            </div>
            <div class="title-line">
                <text class="title">上海智荟信息技术有限公司</text>
            </div>
            <!-- <div class="content-line" @click="checkVersion()">
                <text class="left-txt">版本</text>
                <text class="content-txt">1.1</text>
            </div> -->
             <!-- <div class='line'></div> -->
            <div class="content-line">
                <text class="left-txt">邮箱</text>
                <text class="content-txt">ruby08122@163.com</text>
            </div>
             <div class='line'></div>
            <div class="content-line">
                <text class="left-txt">客服电话</text>
                <text class="content-txt">12378438908</text>
            </div>
             <div class='line'></div>
        </div>

    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
var nativeUtils = weex.requireModule('nativeUtils')

export default {
    mixins: [mixin],
    components: {NavBar},
    methods: {
        checkVersion(){
            nativeUtils.checkVersion()
        }
    }
}
</script>

<style scoped>
    .section {
        width: 710px;
        height: 1100px;
        margin-top: 20px;
        margin-left: 20px;
        margin-bottom: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        background-color: #fff;
    }

    .logo-line {
        width: 710px;
        height: 280px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .logo {
        width: 200px;
        height: 200px;
        border-radius: 100px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .title-line {
        width: 710px;
        margin-top: 20px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .title {
        font-size: 28px;
        color: #0f3691;
    }

    .content-line {
        width: 710px;
        height: 100px;
        margin-top: 30px;
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

    .content-txt {
        font-size: 24px;
        color: #646464;
        margin-right: 20px;
    }
    .line {
        height: 1px;
        width: 740px;
        margin-left: 10px;
        background-color: lightgray;
    }
</style>
