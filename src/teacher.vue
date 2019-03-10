<template>
    <div v-if="userInfo">
        <nav-bar show-back="true" title="老师介绍"></nav-bar>
        <scroller class="list">
            <div class="top">
                <div class="cell">
                    <image class="avatar"
                           :src="userInfo.headimgurl"></image>
                    <div class="add" @click="onMessage">+ 私信</div>
                </div>
                <div class="cell2">
                    <text style="font-size:32px;color:#ffffff;margin-right:10px">{{userInfo.nickname}}</text>
                    <text style="font-size:24px;color:#ffffff;margin-left:10px">{{userInfo.jobTitle}}</text>
                </div>
                <text class="country">{{userInfo.country+' '+userInfo.city}}
                </text>
                <!-- <div class="cell2">
                    <div class="feature-btn" style="margin-right:20px;" v-for="(label, index) in labels"
                         v-bind:key="index">
                        <text style="font-size:24px;color:#ffffff;">{{label}}</text>
                    </div>
                </div> -->
                <div class="cell2">
                    <text style="font-size:24px;color:#ffffff;">{{label}}</text>
                </div>
            </div>
            <div class='tag-view'>
                <div class='tag-btn' v-for="(item,index) in taglist" :key="index" @click="onTagClicked(index)">
                    <text :class="['tag-text', tagIndex==index?'tag-text-selected':'']">{{item}}</text>
                    <div :class="['tag-line', tagIndex==index?'tag-line-selected':'']"></div>
                </div>
            </div>
            <div class="line"></div>
            <slider class="slider" :infinite="false" :index="tagIndex" @change="change">
                <div class='intro'>
                    <!--<image class="video-image" src=""></image>-->
                    <div class='line-gap'></div>
                    <cell-category-header title="个人简介"></cell-category-header>
                    <text class='bg-text'>{{introduction}}</text>
                    <div class='line-gap'></div>

                    <cell-category-header title="过往客户"></cell-category-header>
                    <text class='bg-text'>{{customerIntroduction}}</text>
                    <div class='line-gap'></div>

                    <cell-category-header title="语言能力"></cell-category-header>
                    <text class='bg-text'>{{userInfo.language}}</text>
                    <div class='line-gap'></div>

                    <cell-category-header title="从业经验"></cell-category-header>
                    <text class='bg-text'>{{workExperienceString}}</text>
                </div>
                <div class="course">
                    <div class="course-cell-title">
                        <text :style="{fontSize:'24px',color:'#646464'}">已发布{{publishedList.length}}个</text>
                    </div>
                    <div class="published-wrap">
                        <div class="published-cell" v-for="(item,index) in publishedList" :key="index" @click="onDetail(item)">
                            <image class="published-cell-course-img" :src="item.courseInfo.image"></image>
                            <text class="published-cell-course-title">{{item.courseInfo.name}}</text>
                            <div class="published-cell-course-line">
                                <div class="btn-type">
                                    <text :style="{fontSize:'24px',color:'#ffffff'}">{{CourseTypeString(item.courseInfo.type)}}</text>
                                </div>
                                <text :style="{fontSize:'24px',color:'#969696',marginLeft:'20px'}">{{item.enrollNum}}人学过</text>
                            </div>
                            <text :style="{fontSize:'28px',color:'#ff3c2d'}">{{item.courseInfo.price}}</text>
                        </div>
                    </div>
                    <div class="course-cell-title">
                        <text :style="{fontSize:'24px',color:'#646464'}">未发布{{nonPublishedList.length}}个</text>
                    </div>
                    <div class='non-published-cell' v-for="(item, index) in nonPublishedList" :key="index">
                        <text class='non-published-text' lines='1'>{{item.courseInfo.name}}</text>
                    </div>
                </div>
            </slider>
        </scroller>
        <comment-dialog @commentSubmit="commentSubmit" title="私信" @commentCancel="commentCancel"
                        v-if="showCommentDialog"></comment-dialog>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import CellCategoryHeader from './components/cell-category-header.vue'
import mixin from '@/modules/mixin'
import api from '@/modules/api'
import CommentDialog from './components/CommentDialog'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, CellCategoryHeader, CommentDialog},
  data () {
    return {
      userInfo: null,
      userDetail: null,
      id: 1,
      labels: [],
      label:'',
      tagIndex: 0,
      taglist: ['介绍', '课程'],
      language: '中文，英文',
      workExperience: null,
      publishedList: [],
      nonPublishedList: [],
      showCommentDialog: false
    }
  },
  computed: {
    introduction () {
      if (!this.userDetail) return ''
      if (!this.userDetail.introduction) return ''
      return this.userDetail.introduction
    },
    customerIntroduction () {
      if (!this.userDetail) return ''
      if (!this.userDetail.customerIntroduction) return ''
      return this.userDetail.customerIntroduction
    },
    workExperienceString () {
      let r = ''
      if (!this.workExperience) return ''
      for (let i = 0; i < this.workExperience.length; i++) {
        let item = this.workExperience[i]
        r += this.formatDate(item.startDate / 1000) + '-' + this.formatDate(item.endDate / 1000)
        r += '\n' + item.introduction
        r += '\n\n'
      }
      return r
    }
  },
  mounted () {
    this.getInfo()
    this.getMyCourses()
  },
  created () {
  },
  methods: {
    onDetail (item) {
      navigator.push({
        url: api.getUri('detail') + '?courseId=' + item.courseInfo.id,
        animated: true
      })
    },
    onTagClicked (index) {
      this.tagIndex = index
    },
    getInfo () {
      let self = this
      api.get('/edu/user/getUserDetail', {id: this.id},
        function (data) {
          self.userInfo = data.content.userInfo
          self.userDetail = data.content.userDetail
          if (data.content.userInfo.label) {
            // self.labels = data.content.userInfo.label.split(',')
            self.label = data.content.userInfo.label
          }
          self.workExperience = data.content.workExperience
        },
        function () {

        })
    },
    getMyCourses () {
      let self = this
      api.get('/edu/course/getCoursePageByTeacher', {id: this.id, pageCount: 4, status: 1},
        function (data) {
          for (let i = 0; i < data.content.records.length; i++) {
            let item = data.content.records[i]
            self.publishedList.push(item)
          }
        },
        function () {

        })
    },
    onMessage () {
      this.showCommentDialog = true
    },
    commentCancel () {
      this.showCommentDialog = false
    },
    commentSubmit (e) {
      let self = this
      api.post('/edu/message/addMessage',
        {
          sendToIds: this.id,
          sendFrom: user.userId(),
          content: e,
          type: 3
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '私信发送成功',
            duration: 1
          })
          self.showCommentDialog = false
        },
        function () {
          weex.requireModule('modal').toast({
            message: '私信发送失败，请稍后再试',
            duration: 1
          })
        })
    },
    change (e) {
      this.tagIndex = e.index
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
    }

    .slider {
        flex-direction: row;
    }

    .top {
        width: 750px;
        height: 450px;
        background-color: #0f3691;
        align-items: center;
        justify-content: flex-start;
    }

    .cell {
        width: 750px;
        height: 200px;
        flex-direction: row;
        align-items: flex-start;
        padding-top: 30px;
        justify-content: space-between;
    }

    .cell2 {
        width: 750px;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .avatar {
        width: 150px;
        height: 150px;
        border-radius: 75px;
        border-style: solid;
        border-width: 2px;
        margin-left: 300px;
    }

    .add {
        width: 120px;
        height: 50px;
        margin-right: 20px;
        border-radius: 5px;
        background-color: #ffffff;
        justify-content: center;
        align-items: center;
        font-size: 24px;
        color: #0f3691;
    }

    .feature-btn {
        width: 120px;
        height: 50px;
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
        border-color: rgb(230, 230, 230);
        justify-content: center;
        align-items: center;
        margin-bottom: 50px;
    }

    .intro {
        width: 750px;
    }

    .course {
        width: 750px;
    }

    .tag-view {
        width: 750px;
        height: 80px;
        display: flex;
        flex-direction: row;
    }

    .tag-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: flex-end;
        flex-direction: column;
    }

    .country {
        font-size: 24px;
        color: #ffffff;
        margin-top: 20px;
        margin-bottom: 20px
    }

    .tag-text {
        margin-bottom: 20px;
        font-size: 28px;
    }

    .tag-text-selected {
        color: #0f3691;
    }

    .tag-line {
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tag-line-selected {
        opacity: 1;
    }

    .line {
        width: 750px;
        height: 2px;
        background-color: #d2d2d2;
    }

    .line-gap {
        width: 750px;
        height: 5px;
    }

    .bg-text {
        margin-left: 30px;
        margin-right: 30px;
        margin-bottom: 30px;
        font-size: 24px;
        color: #646464;
    }

    .intro {
        display: flex;
        flex-direction: column;
        padding: 20px;
    }

    .video-image {
        width: 690px;
        height: 400px;
        background-color: rgb(0, 0, 0);
        opacity: 0.75;
        margin-left: 10px;
    }

    .course-cell-title {
        width: 750px;
        height: 60px;
        background-color: #f3f2f4;
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        font-size: 24px;
        color: #646464;
    }

    .published-wrap {
        flex-wrap: wrap;
        width: 750px;
        flex-direction: row;
    }

    .published-cell {
        padding: 10px;
        width: 370px;
        height: 430px;
    }

    .published-cell-course-title {
        margin-top: 10px;
        font-size: 28px;
        color: #333333;
        lines: 2;
    }

    .published-cell-course-img {
        width: 350px;
        height: 220px;
        border-radius: 5px;
        background-color: #d2d2d2;
    }

    .published-cell-course-line {
        width: 340px;
        height: 60px;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
    }

    .btn-type {
        width: 80px;
        height: 40px;
        border-radius: 5px;
        background-color: #0f3691;
        font-size: 24px;
        color: #ffffff;
        align-items: center;
        justify-content: center;
    }

    .non-published-cell {
        width: 710px;
        height: 80px;
        margin-top: 20px;
        margin-right: 20px;
        margin-left: 20px;
        box-shadow: 0px 0px 20px 0px rgba(26, 53, 140, 0.15);
        border-color: #d2d2d2;
        border-width: 1px;
        border-style: solid;
        flex-direction: row;
        align-items: center;
        justify-content: flex-start;
        padding-left: 20px;
        padding-right: 20px;
    }

    .non-published-text {
        font-size: 24px;
        color: #333333;
    }
</style>
