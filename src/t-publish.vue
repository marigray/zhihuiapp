<template>
    <div class="publish-wrapper">
        <nav-bar show-back="true" title="发布"></nav-bar>
        <scroller>
            <div class="content">
                <div class="detail">
                    <text class="detail-txt">课程名称</text>
                    <input class="input" :value="name" :disabled="true"></input>
                </div>
                <text class="publish-title">请选择课程类型</text>
                <div class="detail" v-for="(item,index) in typeName" :key="index" @click="toggleSelected(index)">
                    <text class="detail-txt">{{item}}</text>
                    <image class="icon"
                           :src="type===index?chooseOption.selectIcon:chooseOption.icon"></image>
                </div>
                <div class="detail2" @click="pickVideo" v-if="type==0">
                    <text class="unselect-txt">上传多媒体文件</text>
                    <image class="icon" :src="iconPath('icon_video')"></image>
                    <div class="picture-container">
                        <image :src="videoImageURL" class="image-intro"></image>
                    </div>
                </div>
                <div class="detail" v-if="type==1">
                    <text class="detail-txt">输入详细地址</text>
                    <input class="input" @input="addressInput"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <text class="publish-title" v-if="type!=2">请选择开课时间</text>
                <div class="detail" v-if="type!=2">
                    <text class="detail-txt">开始时间</text>
                    <input class="input" type="date" @input="startdateInput"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <div class="detail" v-if="type!=2">
                    <text class="detail-txt">结束时间</text>
                    <input class="input" type="date" @input="enddateInput"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <div v-if="type==2">
                    <div v-for="(item, index) in chapters" :key="index">
                        <text class="publish-title">章节{{index+1}}</text>
                        <div class="detail">
                            <text class="detail-txt">章节名称</text>
                            <input class="input" v-model="item.chaptername"></input>
                            <image class="icon" :src="iconPath('icon_pen')"></image>
                        </div>
                        <div class="detail">
                            <text class="detail-txt">授课时间</text>
                            <input class="input" type="date" v-model="item.time"></input>
                            <image class="icon" :src="iconPath('icon_pen')"></image>
                        </div>
                        <div class="detail2" @click="picChapterVideo(item)">
                            <text class="unselect-txt">上传多媒体文件</text>
                            <image class="icon" :src="iconPath('icon_video')"></image>
                            <div class="picture-container">
                                <image :src="item.chapterImageURL" class="image-intro"></image>
                            </div>
                        </div>
                        <div class="detail">
                            <text class="detail-txt">视频名称</text>
                            <input class="input" v-model="item.videoname"></input>
                            <image class="icon" :src="iconPath('icon_pen')"></image>
                        </div>
                    </div>
                </div>
                <!--<div class="next-chapter-btn" v-if="type==2" @click="addChapter">-->
                    <text class="next-chapter-btn"  @click="addChapter" v-if="type==2">添加下一章节</text>
                <!--</div>-->
                <text class="publish-title">请输入其他信息</text>
                <div class="detail">
                    <text class="detail-txt">课程售价</text>
                    <input class="input" type="number" @input="priceInput"></input>
                    <text class="unit">元</text>
                </div>
                <div class="detail">
                    <text class="detail-txt">最大报名人数</text>
                    <input class="input" type="number" @input="totalNumInput"></input>
                    <text class="unit">人</text>
                </div>
                <div class="container" style="position: relative">
                    <text class="publish-title">添加课程问卷</text>
                    <!--<text class="publish-pan" style="position: absolute;right: 0;top:27px">管理模版</text>-->
                </div>
                <div class="detail" @click="addQuestionnaire">
                    <text class="detail-txt">前往添加</text>
                    <text class="input">{{questionnaireName}}</text>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <!--<div class="detail" @click="choiceQuestionnaire2">-->
                    <!--<text class="detail-txt">请选择课后试卷模版</text>-->
                    <!--<text class="input">{{questionnaireName2}}</text>-->
                    <!--<image class="icon" :src="iconPath('icon_right')"></image>-->
                <!--</div>-->
            </div>
            <div class="btn" @click="publishText">
                <text style="font-size: 28px;color: #ffffff;">发布</text>
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
    return {
      courseId: 0,
      name: '',
      startDate: '',
      endDate: '',
      price: '',
      totalNum: '',
      area: '',
      address: '',
      typeName: ['线上', '线下', '专栏'],
      type: 0,
      videoImageURL: null,
      videoQiNiuKey: null,
      questionnaireName: '',
      chapters: []
    }
  },
  computed: {
    chooseOption () {
      return {
        icon: this.iconPath('icon_choice'),
        selectIcon: this.iconPath('icon_choice_o')
      }
    }
  },
  created () {
    let self = this
    var globalEvent = weex.requireModule('globalEvent');
    globalEvent.addEventListener('questionnaire-added', function (e) {
      let result = e.result
      self.questionnaireName = result.name
      api.post('/edu/survey/addSurveyCourse',
        {
          surveyid: result.id,
          cid: self.courseId
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '问卷添加成功',
            duration: 1000
          })
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e.msg,
            duration: 1000
          })
        })
    }.bind(this))
  },
  methods: {
    toggleSelected (index) {
      this.type = index
    },
    nameInput (e) {
      this.name = e.value
    },
    startdateInput (e) {
      this.startDate = e.value + ' 00:00:00'
    },
    priceInput (e) {
      this.price = e.value
    },
    totalNumInput (e) {
      this.totalNum = e.value
    },
    enddateInput (e) {
      this.endDate = e.value + ' 00:00:00'
    },
    areaInput (e) {
      this.area = e.value
    },
    addressInput (e) {
      this.address = e.value
    },
    publishText () {
      let self = this
      let parameters = {
        userid: user.userId(),
        id: this.courseId,
        type: this.type + 1, // 1：直播 2.线下 3.专栏
        price: this.price,
        totalNum: this.totalNum,
        country: this.area,
        address: this.address,
        video: this.videoQiNiuKey
      }
      if (this.type !== 2) {
        parameters.startDate = this.startDate
        parameters.endDate = this.endDate
      }
      api.post1('/edu/course/publishApply',
        parameters,
        self.type === 2 ? this.chapters : [], // 此参数放入body
        function (data) {
          weex.requireModule('modal').toast({
            message: '发布成功',
            duration: 1000
          })
          weex.requireModule('myevent').sendGlobal('course-changed', {})
          navigator.pop()
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e,
            duration: 1000
          })
        })
    },
    pickVideo () {
      let self = this
      weex.requireModule('file').pickVideo(
        {name: '/' + user.userId() + '/' + this.courseId},
        function (thumbnailPath) {
          self.videoImageURL = thumbnailPath
        },
        function (key) {
          self.videoQiNiuKey = key
        })
    },
    picChapterVideo (item) {
      weex.requireModule('file').pickVideo(
        {name: '/' + user.userId() + '/' + this.courseId},
        function (thumbnailPath) {
          item.chapterImageURL = thumbnailPath
        },
        function (key) {
          item.url = key
        })
    },
    addQuestionnaire () {
      navigator.push({
        url: api.getUri('questionnaire'),
        animated: true
      })
    },
    addChapter () {
      this.chapters.push({cid: this.courseId, chaptername: '', time: '', url: '', videoname: '', chapterImageURL: ''})
    }
  }
}
</script>

<style scoped>

    .next-chapter-btn {
        width: 710px;
        height: 70px;
        border-radius: 5px;
        margin-top: 40px;
        margin-bottom: 20px;
        justify-content: center;
        align-items: center;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: dashed;
        background-color: white;
        color: rgb(15, 54, 145);
        text-align: center;
        line-height: 70px;
    }

    .content {
        display: flex;
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
    }

    .detail {
        width: 710px;
        height: 90px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .detail-txt, .unit {
        font-size: 24px;
        color: #333333;
    }

    .input {
        font-size: 24px;
        height: 50px;
        color: #333333;
        flex: 1;
        margin-left: 50px;
        margin-right: 50px;
        text-align: right;
        line-height: 50px;
    }

    .publish-pan {
        width: 120px;
        padding: 4px 10px;
        background-color: rgb(15, 54, 145);
        border-radius: 5px;
        font-size: 24px;
        color: #fff;
    }

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
    }

    .publish-title {
        margin-top: 20px;
        background-color: #e8eaf3;
        color: rgb(15, 54, 145);
        padding: 5px 0;
    }

    .btn {
        margin-top: 50px;
        margin-bottom: 20px;
        margin-left: 30px;
        width: 690px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        font-size: 28px;
        color: #ffffff;
    }

    .detail2 {
        width: 710px;
        height: 180px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
        padding-top: 20px;
        margin-top: 20px;
    }

    .unselect-txt {
        font-size: 24px;
        color: #333333;
    }

    .image-intro {
        margin-top: 15px;
        margin-left: 15px;
        width: 100px;
        height: 100px;
    }

    .picture-container {
        flex-direction: row;
    }
</style>
