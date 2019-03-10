<template>
    <div class='warpper'>
        <navbar title="学习"></navbar>
        <div class='tab'>
            <div class='tab-btn' v-for="(item, index) in tabList" :key="index" @click="tabClicked(index)">
                <image class='tab-image' :src="tabIndex==index?item.selectIcon:item.icon"></image>
                <text :class="['tab-title', tabIndex==index?'tag-title-selected':'']">{{item.title}}</text>
            </div>
        </div>
        <slider class="slider" @change="change" :index="tabIndex">
            <!-- 在线课程 -->
            <list class='list' @loadmore="loadMoreCourses">
                <cell class='cell1' v-for="(item,index) in myCourses" :key="index"
                      @click="jumpDetail(item.enrollInfo.course)">
                    <div class="cell-container">
                        <div class='cell1-top'>
                            <text class='cell1-teacher'>{{item.userInfo.name}}</text>
                            <div class='cell1-gap'></div>
                            <text class='cell1-date'>{{formatDate(item.enrollInfo.course.startDate/1000)}}</text>
                        </div>
                        <div class='cell1-line'></div>
                        <div class='cell1-bottom'>
                            <image class='cell1-image' mode="aspectFill" :src="item.enrollInfo.course.image">
                            </image>
                            <div class='cell1-bottom-right'>
                                <text class='cell1-content'>{{item.enrollInfo.course.name}}</text>
                                <div class='cell1-gap'></div>
                                <text class='cell1-type'>{{CourseTypeString(item.enrollInfo.course.type)}}</text>
                                <div class='cell1-gap'></div>
                                <text class='cell1-content'>{{item.enrollInfo.course.type==2?item.enrollInfo.course.address:''}}</text>
                                <div class='cell1-gap'></div>
                                <div class="row-right-line4">
                                    <!--<div :class="['blue-btn','blue-btn-margin']" @click="jumpEvaluate(item.enrollInfo.course)">-->
                                        <!--<text class="blue-text">评价</text>-->
                                    <!--</div>-->
                                </div>
                            </div>
                        </div>
                    </div>
                </cell>
                <cell class="load-cell">
                    <more-footer :status="coursesLoadingStatus"></more-footer>
                </cell>
            </list>
            <!-- 我的关注 -->
            <list class='list' @loadmore="loadMoreFocuses">
                <cell class='cell2' v-for="(item,index) in myFocuses" :key="index" @click="jumpTeacher(item)">
                    <div class="cell-container">
                        <div class='cell2-top'>
                            <image class='cell2-header' :src="item.userInfo.headimgurl"></image>
                            <div class='cell2-top-right'>
                                <text class='cell2-nickname'>{{item.userInfo.name}}</text>
                                <text class='cell2-liked'>{{item.collectionCount}}人关注</text>
                            </div>
                        </div>
                        <div class='cell2-bottom'>
                            <text class='cell2-intro'>{{item.introduction?item.introduction: '暂无相关介绍'}}</text>
                        </div>
                    </div>
                </cell>
                <cell class="load-cell">
                    <more-footer :status="focusLoadingStatus"></more-footer>
                </cell>
            </list>
        </slider>
    </div>
</template>

<script>
import navbar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import user from '@/modules/user'
import MoreFooter from './components/more-footer'

var navigator = weex.requireModule('navigator')
export default {
  mixins: [mixin],
  components: {navbar, MoreFooter},
  name: 'study',
  data () {
    return {
      tabIndex: 0,
      myCourses: [],
      myFocuses: [],
      coursesLoadingStatus: 'loading',
      focusLoadingStatus: 'loading',
      focusPageNo: 1,
      coursePageNo: 1
    }
  },
  created () {
    this.getMyFocuses()
    this.getMyCourses()
    var globalEvent = weex.requireModule('globalEvent')
    globalEvent.addEventListener('user-changed', function () {
      this.resetMyCourses()
      this.resetMyFocuses()
    }.bind(this))
    globalEvent.addEventListener('pay-result', function (e) {
      // let type = e.result.type
      // let cid = e.result.courseid
      let result = e.result.result
      if (result) {
        this.resetMyCourses()
      }
    }.bind(this))
  },
  methods: {
    change (e) {
      this.tabIndex = e.index
    },
    tabClicked (index) {
      this.tabIndex = index
    },
    jumpDetail (item) {
      navigator.push({
        url: api.getUri('detail1') + '?courseId=' + item.id,
        animated: true
      })
    },
    jumpTeacher (item) {
      navigator.push({
        url: api.getUri('teacher') + '?id=' + item.userInfo.id,
        animated: true
      })
    },
    resetMyCourses () {
      this.coursesLoadingStatus = 'loading'
      this.coursePageNo = 1
      this.myCourses = []
      this.getMyCourses()
    },
    loadMoreCourses () {
      if (this.coursesLoadingStatus === 'no-more' || this.coursesLoadingStatus === 'no-data') return
      this.coursePageNo++
      this.getMyCourses()
    },
    getMyCourses () {
      let self = this
      api.get('/edu/course/GetEnrollByUser', {userid: user.userId(), status: 0, pageNo: this.coursePageNo, pageCount: 10},
        function (data) {
          self.myCourses = self.myCourses.concat(data.content.records)
          if (self.myCourses.length === 0) {
            self.coursesLoadingStatus = 'no-data'
          } else {
            if (data.content.total === self.myCourses.length) {
              self.coursesLoadingStatus = 'no-more'
            }
          }
        },
        function () {
          self.coursesLoadingStatus = 'error'
        })
    },
    // 分割线
    loadMoreFocuses () {
      if (this.focusLoadingStatus === 'no-more' || this.focusLoadingStatus === 'no-data') return
      this.focusPageNo++
      this.getMyFocuses()
    },
    resetMyFocuses () {
      this.focusLoadingStatus = 'loading'
      this.myFocuses = []
      this.focusPageNo = 1
      this.getMyFocuses()
    },
    jumpEvaluate (item) { // 课程评价
      navigator.push({
        url: api.getUri('course-comments' + '?courseId=' + item.id + '&navTitle=' + item.name),
        animated: true
      })
    },
    getMyFocuses () {
      let self = this
      api.get('/edu/collection/getCollectionPage', {uid: user.userId(), type: 2, pageNo: this.focusPageNo, pageCount: 10},
        function (data) {
          self.myFocuses = self.myFocuses.concat(data.content.records)
          if (self.myFocuses.length === 0) {
            self.focusLoadingStatus = 'no-data'
          } else {
            if (data.content.total === self.myFocuses.length) {
              self.focusLoadingStatus = 'no-more'
            }
          }
        },
        function () {
          self.focusLoadingStatus = 'error'
        })
    }
  },
  computed: {
    tabList () {
      return [
        {
          title: '已购课程',
          icon: this.iconPath('icon_study'),
          selectIcon: this.iconPath('icon_study_o')
        },
        {
          title: '我的关注',
          icon: this.iconPath('icon_gz'),
          selectIcon: this.iconPath('icon_gz_o')
        }
      ]
    }
  }

}
</script>

<style scoped>
    .list {
        flex: 1;
    }

    .slider {
        width: 750px;
        flex: 1;
        height: 1000px;
    }

    .warpper {
        flex: 1;
        background-color: white;
    }

    .tab {
        height: 220px;
        width: 750px;
        flex-direction: row;
        display: flex;
    }

    .tab-btn {
        display: flex;
        flex: 1;
        justify-content: center;
        align-items: center;
        flex-direction: column;
    }

    .tab-image {
        width: 100px;
        height: 100px;
    }

    .tab-title {
        font-size: 28px;
        margin-top: 10px;
        text-align: center;
    }

    .tag-title-selected {
        color: #0f3691;
    }

    .cell-container {
        flex: 1;
        background-color: rgb(249, 249, 249);
        border-radius: 10px;
    }

    .cell1 {
        padding: 20px;
        background-color: white;
        height: 360px;
    }

    .cell1-top {
        height: 80px;
    }

    .cell1-line {
        height: 1px;
        background-color: lightgray;
    }

    .cell1-top {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .cell1-teacher {
        margin-left: 20px;
        font-size: 28px;
        color: #333;
    }

    .cell1-gap {
        flex: 1;
    }

    .cell1-date {
        font-size: 28px;
        color: #333;
        margin-right: 20px;
    }

    .cell1-bottom {
        display: flex;
        flex-direction: row;
    }

    .cell1-bottom-right {
        display: flex;
        margin-top: 20px;
        margin-bottom: 20px;
        flex: 1;
        flex-direction: column;
        height: 180px;
    }

    .cell1-image {
        margin: 20px;
        width: 250px;
        height: 180px;
        border-radius: 10px;
    }

    .cell1-content {
        font-size: 24px;
    }

    .cell1-type {
        display: block;
        font-size: 20px;
        color: white;
        width: 60px;
        padding: 8px;
        text-align: center;
        border-radius: 5px;
        background-color: #0f3691;
    }

    .cell1-price {
        font-size: 28px;
        color: #ec5f59;
    }

    /* 第二类型cell */

    .cell2 {
        background-color: white;
        height: 278px;
        border-radius: 1px;
        padding: 20px;
    }

    .cell2-top {
        display: flex;
        flex-direction: row;
    }

    .cell2-top-right {
        margin-top: 30px;
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    .cell2-header {
        margin: 20px;
        width: 100px;
        height: 100px;
        border-radius: 50px;
    }

    .cell2-nickname {
        font-size: 32px;
    }

    .cell2-liked {
        margin-top: 10px;
        text-align: left;
        font-size: 24px;
    }

    .cell2-bottom {

    }

    .cell2-intro {
        margin-left: 20px;
        margin-top: 10px;
        margin-right: 20px;
        font-size: 24px;
    }

    .row-right-line4 {
        width: 410px;
        height: 40px;
        flex-direction: row;
    }

    .blue-btn {
        width: 80px;
        height: 40px;
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
        border-color: #0f3691;
        background-color: #ffffff;
        align-items: center;
        justify-content: center;
    }

    .blue-btn-margin {
        margin-left: 0px;
    }

    .blue-text {
        font-size: 24px;
        color: #0f3691;
    }
</style>
