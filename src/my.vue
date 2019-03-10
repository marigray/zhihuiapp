<template>
    <div @viewappear="viewappear">
        <nav-bar title="个人中心"></nav-bar>
        <scroller class="scroller">
            <div class='user-section' @click="login">
                <image class='user-header'
                       :src="userHeader()"></image>
                <div class="nick-name-container" v-if="userInfo">
                    <text class='nick-name'>{{userInfo.name}}</text>
                    <!-- <text class='login-way'>正在使用手机登录</text> -->
                </div>
                <div class="nick-name-container" v-else>
                    <text class='click-login'>点我登录</text>
                </div>
            </div>
            <div v-for="(item, index) in titleList" class='section' :key="index">
                <div v-for="(subitem, subIndex) in item" class="cell" :key="subIndex"
                     @click="onSubItemClick(subitem.link)">
                    <div class="cell-left-container">
                        <image :src=subitem.icon class="cell-icon"></image>
                        <text class="cell-text">{{subitem.title}}</text>
                    </div>
                    <div class="cell-right-container">
                        <text>{{subitem.content}}</text>
                        <image src="file:///android_asset/images/content/icon_right_black.png"></image>
                    </div>
                </div>
            </div>
        </scroller>
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
    return {userInfo: null}
  },
  methods: {
    onSubItemClick (link) {
      if (link === 'material' ||link === 'bindingphone' ||link === 'interest' || link === 'course-require' || link === 'myorder' || link === 'invoice1') {
        if (!this.isLogined()) {
          this.pushLogin()
          return
        }
      }
      if(link === 'password1'){
        if(this.userInfo.phone=='null'||this.userInfo.phone == null){
          weex.requireModule('modal').toast({
            message: '请先绑定手机号',
            duration: 1000
          })
          navigator.push({
            url: api.getUri('bindingphone'),
            animated: true
          })
        }else{
          navigator.push({
            url: api.getUri(link) +'?telOrMail=' + this.userInfo.phone + '&isModify=' + true,
            animated: true
          })
        }
      }else{
        navigator.push({
          url: api.getUri(link) +'?phone=' + this.userInfo.phone,
          animated: true
        })
      }
    },
    login () {
      if (!this.isLogined()) {
        this.pushLogin()
      }
    },
    viewappear () {
      this.getUserDetail()
    },
    getUserDetail () {
      if (!this.isLogined()) {
        this.userInfo = null
        return
      }
      let self = this
      api.get('/edu/user/getUserDetail', {id: 298},
        function (data) {
          self.userInfo = data.content.userInfo
        },
        function () {

        })
    },
    userHeader () {
      if (this.isLogined()) {
        if (this.userInfo && this.userInfo.headimgurl) return this.userInfo.headimgurl
      }
      return this.iconPath('logo')
    }
  },
  computed: {
    titleList () {
      return [
        [{
          'contentColor': '#999999',
          'icon': this.iconPath('zh_xxxq'),
          'isNeedLogin': 1,
          'link': 'material',
          'showRedPointType': 0,
          'title': '完善个人资料'
        },
        {
          'contentColor': '#999999',
          'icon': this.iconPath('login_iphone'),
          'isNeedLogin': 1,
          'link': 'bindingphone',
          'showRedPointType': 0,
          'title': '绑定手机'
        },
        {
          'contentColor': '#999999',
          'icon': this.iconPath('login_password'),
          'isNeedLogin': 1,
          'link': 'password1',
          'showRedPointType': 0,
          'title': '修改密码'
        }],
        [{
          'contentColor': '#999999',
          'icon': this.iconPath('zh_xxxq'),
          'isNeedLogin': 1,
          'link': 'interest',
          'showRedPointType': 0,
          'title': '学习兴趣'
        }],
        [{
          'contentColor': '#999999',
          'icon': this.iconPath('zh_xx'),
          'isNeedLogin': 1,
          'link': 'message',
          'showRedPointType': 0,
          'title': '消息中心'
        }
        ],
        [
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_fbxq'),
            'isNeedLogin': 1,
            'link': 'course-require',
            'showRedPointType': 0,
            'title': '发布课程需求'
          },
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_kcdd'),
            'isNeedLogin': 1,
            'link': 'myorder',
            'showRedPointType': 0,
            'title': '课程订单'
          },
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_fp'),
            'isNeedLogin': 0,
            'link': 'invoice1',
            'showRedPointType': 0,
            'title': '发票管理'
          }],
        [
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_set'),
            'isNeedLogin': 0,
            'link': 'setting',
            'showRedPointType': 0,
            'title': '设置'
          },
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_help'),
            'isNeedLogin': 0,
            'link': 'help1',
            'showRedPointType': 0,
            'title': '帮助与反馈'
          },
          {
            'content': '',
            'contentColor': '#999999',
            'icon': this.iconPath('zh_hp'),
            'isNeedLogin': 0,
            'link': '',
            'showRedPointType': 0,
            'title': '给我们好评'
          }]
      ]
    }
  }
}
</script>

<style scoped>
    .scroller {
        background-color: rgb(240, 240, 240);
    }

    .section {
        margin-bottom: 20px;
    }

    .cell {
        background-color: white;
        flex-direction: row;
        background-color: white;
        padding: 28px 20px;
        margin-left: 20px;
        margin-right: 20px;
        border: 1px solid #eee;
        border-radius: 5px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        height: 100px;
    }

    .cell-left-container,
    .cell-right-container {
        display: flex;
        align-items: center;
        flex-direction: row;
    }

    .cell-icon {
        width: 50px;
        height: 50px;
    }

    .cell-text {
        margin-left: 20px;
        font-size: 28px;
        color: black;
    }

    .title-bar-des,
    .title-bar-de {
        display: inline-block;
    }

    .cell-left-container {
        width: 400px;
        height: 50px;
        margin-right: 16px;
    }

    .cell-right-container {
        width: 12px;
        height: 22px;
        margin-left: 16px;
    }

    .user-header {
        width: 150px;
        height: 150px;
        margin-left: 20px;
        margin-top: 50px;
        margin-bottom: 50px;
        border-radius: 75px;
        border-width: 1px;
        border-color: rgb(200, 200, 200);
        border-style: solid;
    }

    .user-section {
        border: 1px solid #eee;
        margin-bottom: 20px;
        box-shadow: 0px 0px 10px 5px #f0f0f0;
        flex-direction: row;
        align-items: center;
        margin-left: 20px;
        margin-right: 20px;
        margin-top: 20px;
        background-color: white;
    }

    .nick-name-container {
        margin-left: 20px;
    }

    .login-way {
        display: block;
        font-size: 24px;
        color: #969696;
    }

    .click-login {
        display: block;
        font-size: 32px;
        color: #333333;
    }

    .nick-name {
        color: #333333;
        font-size: 32px;
    }
</style>
