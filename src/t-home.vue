<template>
    <div @viewappear="viewappear">
        <nav-bar title="主页"></nav-bar>
        <list class="list">
            <cell>
                <slider class="slider">
                    <div class="poster" v-for="(item, index) in bannerList" v-bind:key="index"
                         @click="jumpDetail(item)">
                        <image class="poster_image" resize="cover" :src="item.image"></image>
                    </div>
                    <indicator class="indicator"></indicator>
                </slider>
            </cell>
            <cell class="message-cell">
                <div class="calendar-content">
                    <div>
                        <text class='calendar-title'>我的日程</text>
                    </div>
                    <div v-for="(item, index) in calendarList" :key="index">
                        <div style="width:670px;height:2px;margin-left:20px;background-color: #d2d2d2;"></div>
                        <div class="calendar-row">
                            <image class="header-icon" :src="iconPath('header_dot')"></image>
                            <div>
                                <text class="calendar-text">{{item.courseInfo.name}}</text>
                                <text class="message-text">{{item.courseInfo.startDate}}-{{item.courseInfo.endDate}}</text>
                            </div>
                        </div>
                    </div>
                    <text v-if="calendarList.length == 0" class="no-calendar-text">暂无日程</text>
                </div>
            </cell>
            <!--消息-->
            <cell class="message-cell">
                <div class="message-content">
                    <div class="message-tab">
                        <div class="message-tab-left">
                            <div class="message-tab-item" @click="platformMsg()">
                                <text :class="['message-tab-text', (messageTabIndex==0)?'message-tab-text-selected':'']">平台通知</text>
                                <div class="message-tab-line" v-if="messageTabIndex==0"></div>
                            </div>
                            <div class="message-tab-item" @click="personalMsg()">
                                <text :class="['message-tab-text', (messageTabIndex==1)?'message-tab-text-selected':'']">我的私信</text>
                                <div class="message-tab-line" v-if="messageTabIndex==1"></div>
                            </div>
                        </div>
                        <text class="more-text" @click="jumpMsg()">更多 ></text>
                    </div>
                    <div class="message-content-line"></div>
                    <div class="message-bottom" @click="jumpMsg()">
                        <text class="message-text" v-if="messageTabIndex==0 && systemMessage">{{systemMessage.message.content}}</text>
                        <text class="message-text" v-if="messageTabIndex==1 && privateMessage">{{privateMessage.message.content}}</text>
                    </div>
                </div>
            </cell>
        </list>
    </div>
</template>

<script>

import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import user from '@/modules/user'

var stream = weex.requireModule('stream')
var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      systemMessage: null,
      privateMessage: null,
      messageTabIndex: 0,
      bannerList: [],
      calendarList: []
    }
  },
  created () {
    this.getBanners()
    this.getSystemMessage()
    this.getPrivateMessage()
    this.getMyScheduleList()
  },
  methods: {
    viewappear () {
      this.getSystemMessage()
      this.getPrivateMessage()
      this.getMyScheduleList()
    },
    getBanners () {
      let self = this
      stream.fetch({
        method: 'GET',
        url: 'http://116.62.23.7:3060/edu/carousel/getCarouselList',
        type: 'json'
      }, function (ret) {
        if (ret.ok) {
          self.bannerList = ret.data.content
        } else {

        }
      })
    },
    platformMsg () {
      this.messageTabIndex = 0
    },
    personalMsg () {
      this.messageTabIndex = 1
    },
    jumpMsg () {
      navigator.push({
        url: api.getUri('message'),
        animated: true
      })
    },
    getSystemMessage () {
      let self = this
      api.get('/edu/message/getSystemMessage', {type: 1},
        function (data) {
          if (data.content.records && data.content.records.length) {
            self.systemMessage = data.content.records[0]
          } else {
            self.systemMessage = {message: {content: '暂无相关消息'}}
          }
        },
        function () {

        })
    },
    getPrivateMessage () {
      let self = this
      api.get('/edu/message/getSystemMessage', {type: 3, userid: user.userId()},
        function (data) {
          if (data.content.records && data.content.records.length) {
            self.privateMessage = data.content.records[0]
          } else {
            self.privateMessage = {message: {content: '暂无相关消息'}}
          }
        },
        function () {

        })
    },
    getMyScheduleList () {
      let self = this
      api.get('/edu/course/getCoursePageByTeacher', {status: 2, id: user.userId(), pageCount: 10, pageNo: 1},
        function (data) {
          if (data.content.records && data.content.records.length) {
            self.calendarList = data.content.records
          } else {
          }
        },
        function () {

        })
    }
  }
}

</script>

<style scoped>
    .list {
        flex: 1;
        background-color: white;
    }

    .slider {
        width: 750px;
        height: 420px;
    }

    .indicator {
        position: absolute;
        margin-top: 390px;
        width: 200px;
        height: 20px;
        left: 275px;
    }

    .poster {
        width: 750px;
        height: 420px;
    }

    .poster_image {
        margin: 20px;
        width: 710px;
        height: 400px;
        border-radius: 10px;
    }

    .calendar-title {
        font-size: 32px;
        color: #0f3691;
        margin-left: 20px;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .calendar-text {
        font-size: 24px;
        color: #0f3691;
    }

    .calendar-row {
        flex-direction: row;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .header-icon {
        width: 10px;
        height: 10px;
        border-radius: 5px;
        margin-top: 10px;
        margin-left: 15px;
        margin-right: 20px;
    }

    .calendar-content {
        margin-top: 20px;
        margin-bottom: 20px;
        width: 710px;
        background-color: white;
        overflow: auto;
        border-radius: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .message-cell {
        align-items: center;
        background-color: rgb(249, 249, 249);
    }

    .message-content {
        margin-top: 20px;
        margin-bottom: 20px;
        width: 710px;
        background-color: white;
        height: 200px;
        border-radius: 10px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
    }

    .message-tab {
        flex-direction: row;
        height: 80px;
        width: 710px;
        justify-content: space-between;
        align-items: center;
    }

    .message-tab-left {
        flex-direction: row;
    }

    .message-tab-item {
        margin-left: 20px;
        margin-right: 10px;
    }

    .message-tab-line {
        height: 4px;
        width: 100%;
        background-color: #0f3691;
    }

    .message-content-line {
        height: 2px;
        background-color: #dcdcdc;
    }

    .more-text {
        font-size: 24px;
        color: #0f3691;
        margin-right: 20px;
    }

    .message-type {
        position: absolute;
        top: 20px;
        left: 20px;
        color: white;
        padding: 0px 8px;
        border-radius: 3px;
        width: 90px;
        height: 30px;
        line-height: 30px;
        background-color: rgb(15, 54, 145);
        font-size: 18px;
    }

    .message-bottom {
        padding: 20px;
    }

    .message-tab-text {
        height: 76px;
        line-height: 76px;
    }

    .message-tab-text-selected {
        color: #0f3691;
    }

    .message-text {
        font-size: 24px;
        color: #646464;
    }

    .teacher-cell {
        width: 750px;
        height: 190px;
        flex-direction: row;
    }

    .teacher-header {
        margin-top: 40px;
        margin-left: 20px;
        margin-bottom: 30px;
        width: 120px;
        height: 120px;
        border-radius: 60px;
    }

    .right-content {
        margin-right: 20px;
        flex: 1;
    }

    .teacher-row1 {
        margin-top: 40px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-name {
        font-size: 24px;
        color: #0f3691;
    }

    .teacher-title {
        margin-left: 20px;
        font-size: 22px;
        color: #969696;
    }

    .teacher-row2 {
        margin-top: 10px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-intro {
        font-size: 28px;
        color: #333333;
    }

    .teacher-row3 {
        height: 35px;
        margin-top: 10px;
        margin-left: 20px;
        flex-direction: row;
        align-items: center;
    }

    .teacher-type {
        color: white;
        padding: 0px 8px;
        border-radius: 5px;
        height: 35px;
        line-height: 35px;
        background-color: #0f3691;
        font-size: 18px;
    }

    .teacher-learned {
        height: 35px;
        line-height: 35px;
        margin-left: 10px;
        font-size: 24px;
        color: #969696;
    }

    .teacher-cell-line {
        display: block;
        margin-left: 20px;
        width: 730px;
        height: 2px;
        background-color: #d2d2d2;
    }

    .course-cell {
        background-color: rgb(249, 249, 249);
    }

    .course-content {
        margin: 30px 20px;
        background-color: white;
        width: 710px;
        height: 300px;
        border-radius: 10px;
        /*border-color: lightgray;*/
        /*border-width: 1px;*/
    }

    .course-image {
        width: 710px;
        height: 240px;
    }

    .course-info {
        height: 60px;
        flex-direction: row;
        align-items: center;
    }

    .course-titlle {
        margin-left: 20px;
        font-size: 24px;
        color: #333333;
    }

    .course-info-gap {
        flex: 1;
    }

    .course-learned {
        margin-left: 10px;
        font-size: 24px;
        color: #969696;
    }

    .course-price {
        font-size: 28px;
        margin-right: 20px;
        color: #ec5f59;
    }

    .no-calendar-text {
        margin-left: 20px;
        margin-bottom: 20px;
    }
</style>
