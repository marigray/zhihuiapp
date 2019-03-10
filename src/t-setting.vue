<template>
    <div>
        <nav-bar show-back="true" title="设置"></nav-bar>
        <div class='content-line'>
            <text class='left-txt'>清除缓存</text>
            <!-- <image class='arrow' :src="iconPath('icon_right_black')"></image> -->
        </div>
        <div class='line'></div>
        <div class="content-line" @click="checkVersion()">
            <text class="left-txt">版本更新</text>
            <text class="content-txt">{{versionName}}</text>
        </div>
        <div class='line'></div>
        <!-- <div class='content-line' @click="about">
            <text class='left-txt'>关于我们</text>
            <image class='arrow' :src="iconPath('icon_right_black')"></image>
        </div>
        <div class='line'></div> -->

        <text class="logout" v-if="isLogin" @click="logout">注销</text>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
var navigator = weex.requireModule('navigator')
var nativeUtils = weex.requireModule('nativeUtils')

export default {
    mixins: [mixin],
    components: { NavBar },
    data() {
        return {
            isLogin: false,
            versionName: ''
        }
    },
    created() {
        this.isLogin = this.isLogined()
        this.versionName = nativeUtils.getVersionName()
    },
    methods: {
        logout() {
            this.isLogin = false
            weex.requireModule('user').logout()
            weex.requireModule('myevent').sendGlobal('user-changed', {})
            weex.requireModule('modal').toast({
                message: '注销成功',
                duration: 1
            })
        },
        about() {
            navigator.push({
                url: api.getUri('about'),
                animated: true
            })
        },
        checkVersion() {
            nativeUtils.checkVersion()
        }
    }
}
</script>

<style scoped>
.btn {
    height: 100px;
    display: flex;
    flex-direction: row;
    align-items: center;
}

.line {
    height: 1px;
    width: 740px;
    margin-left: 10px;
    background-color: lightgray;
}

.btn-text {
    display: flex;
    flex: 1;
    margin-left: 20px;
    font-size: 24px;
    color: #333333;
}

.arrow {
    margin-right: 20px;
    width: 20px;
    height: 20px;
}

.logout {
    margin-top: 100px;
    width: 400px;
    margin-left: 175px;
    height: 80px;
    background-color: red;
    text-align: center;
    line-height: 80px;
    color: white;
    font-size: 35px;
}
.content-line {
    width: 710px;
    height: 100px;
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
</style>
