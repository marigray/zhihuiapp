<template>
    <div>
        <nav-bar title="添加课程" show-back="true" show-right-txt="true" right-txt="课程列表"
                 @onRightTxtClicked="onNavigatorPop()"></nav-bar>
        <scroller>
            <div class='info'>
                <div class="detail">
                    <text class="unselect-txt">请输入课程名称</text>
                    <input class="input" :disabled="!editable" @input="nameInput" :value="name"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <div class="detail">
                    <text class="unselect-txt">请选择适用行业</text>
                    <text class="input" @click="showIndustryPop"
                          :value="type==='add'?'':selectedIndustryName">{{selectedIndustryName}}</text>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail">
                    <text class="unselect-txt">请选择适用职能</text>
                    <text class="input" @click="showFunctionPop"
                          :value="type==='add'?'':selectedFunctionName">{{selectedFunctionName}}</text>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">请输入课程背景</text>
                    <textarea class="textarea" :disabled="!editable" @input="bgInput" :value="type==='add'?'':backGround"></textarea>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">请输入课程受众</text>
                    <textarea class="textarea" :disabled="!editable" @input="audiencesInput" :value="type==='add'?'':audiences"></textarea>
                </div>
                <div class="detail">
                    <text class="unselect-txt">请输入学习收益</text>
                    <input class="input" :disabled="!editable" @input="gainsInput" :value="type==='add'?'':gains"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">其他介绍</text>
                    <textarea class="textarea" :disabled="!editable" @input="targetInput" :value="type==='add'?'':target"></textarea>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">上传课程图片</text>
                    <image class="icon" @click="pickImage(0)" :src="iconPath('icon_camera')"></image>
                    <div style="flex-direction: row">
                        <div class="picture-container" v-if="courseImage">
                            <image :src="courseImage" class="image-intro"></image>
                            <image :src="iconPath('icon_gb_big')" class="image-delete"
                                   @click="deleteCourseImage"></image>
                        </div>
                    </div>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">上传图文介绍</text>
                    <image class="icon" @click="pickImage(1)" :src="iconPath('icon_camera')"></image>
                    <div style="flex-direction: row">
                        <div v-for="(item, index) in pictures" :key="index" class="picture-container">
                            <image :src="item" class="image-intro"></image>
                            <image :src="iconPath('icon_gb_big')" class="image-delete"
                                   @click="deleteDescImage(index)"></image>
                        </div>
                    </div>
                </div>
            </div>
            <div class="btn" @click="addCourse" v-if="editable">
                <text style="font-size: 28px;color: #ffffff;">课程提交</text>
            </div>
        </scroller>

        <wxc-popup :show="isIndustryPopShow" @wxcPopupOverlayClicked="hidePop" pos="bottom" height="500">
            <scroller class="pop-scroller">
                <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in industryList" :key="item.id"
                          :title="item.name" @wxcCellClicked="setCurrentIndustry(item)">
                    <image class="pop-selected" v-if="item.id===selectedIndustryId" :src="iconPath('icon_sus')"></image>
                </wxc-cell>
            </scroller>
        </wxc-popup>

        <wxc-popup :show="isFunctionPopShow" @wxcPopupOverlayClicked="hidePop" pos="bottom" :height="500">
            <scroller class="pop-scroller">
                <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in functionList" :key="item.id"
                          :title="item.name" @wxcCellClicked="setCurrentFunction(item)">
                    <image v-if="item.id===selectedFunctionId" :src="iconPath('icon_sus')"></image>
                </wxc-cell>
            </scroller>
        </wxc-popup>
        <wxc-loading v-if="isSubmitting"></wxc-loading>
        <zhprogress v-if="isPopupProgress" :progressValue=uploadProgressValue></zhprogress>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import zhprogress from './components/zhprogress'
import mixin from '@/modules/mixin'

import Nat from 'natjs'
import {WxcButton, WxcCell, WxcPopup, WxcLoading, WxcProgress, WxcMask} from 'weex-ui'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar, WxcCell, WxcPopup, WxcButton, WxcLoading, WxcProgress, WxcMask,zhprogress},
  data () {
    return {
      industryList: [],
      functionList: [],
      selectedIndustryId: null,
      selectedFunctionId: null,
      selectedIndustryName: '',
      selectedFunctionName: '',
      isIndustryPopShow: false,
      isFunctionPopShow: false,
      pictures: [],
      name: '',
      backGround: '',
      audiences: '', // 受众
      target: '',
      gains: '',
      courseImag: '',
      introduction: '',
      isSubmitting: false,
      courseImage: null,
      courseId: 0,
      type: 'add',
      uploadProgressValue: 0,
      isPopupProgress: false,
      editable: false
    }
  },
  created () {
    this.getIndustryList()
    this.getFunctionList()
    if (this.type !== 'add') {
      this.getCourseList()
    }
  },
  methods: {
    nameInput (e) {
      this.name = e.value
    },
    bgInput (e) {
      this.backGround = e.value
    },
    audiencesInput (e) {
      this.audiences = e.value
    },
    targetInput (e) {
      this.target = e.value
    },
    gainsInput (e) {
      this.gains = e.value
    },
    onNavigatorPop () {
      navigator.pop()
    },
    getIndustryList () {
      let self = this
      api.get('/edu/dic/getIndustryList', null,
        function (data) {
          self.industryList = data.content
        },
        function () {
        })
    },
    getFunctionList () {
      let self = this
      api.get('/edu/dic/getFunctionList', null,
        function (data) {
          self.functionList = data.content
        },
        function () {
        })
    },
    setCurrentIndustry (item) {
      this.selectedIndustryId = item.id
      this.selectedIndustryName = item.name
      this.isIndustryPopShow = false
    },
    setCurrentFunction (item) {
      this.selectedFunctionId = item.id
      this.selectedFunctionName = item.name
      this.isFunctionPopShow = false
    },
    showIndustryPop () {
      if (!this.editable) return
      this.isIndustryPopShow = true
    },

    showFunctionPop () {
      if (!this.editable) return
      this.isFunctionPopShow = true
    },
    hidePop () {
      this.isIndustryPopShow = false
      this.isFunctionPopShow = false
    },
    pickImage (index) {
      if (!this.editable) return
      let self = this
      Nat.image.pick({
        width: 100,
        height: 200
      })
        .then((ret) => {
          let filePath = ret.paths[0]
          weex.requireModule('file').compressor(filePath, function (compressedImageFile) {
            self.uploadProgressValue = 0
            self.isPopupProgress = true
            Nat.upload(api.getBaseUrl() + '/edu/course/uploadPic',
              {
                path: compressedImageFile,
                formData: {
                  type: 1
                },
                name: 'aaaaa.png',
                mimeType: 'png'
              },
              {
                onProgress: (p) => {
                  self.uploadProgressValue = Math.floor(p * 100)
                }
              }, (err, res) => {
                self.isPopupProgress = false
                if (err) {
                  weex.requireModule('modal').toast({
                    message: JSON.stringify(err),
                    duration: 1000
                  })
                  return
                }
                let data = JSON.parse(res.data)
                if (data.httpCode === 200) {
                  if (index === 0) {
                    self.courseImage = data.content
                  } else {
                    self.pictures.push(data.content)
                  }
                } else {
                  weex.requireModule('modal').toast({
                    message: '图片上传失败',
                    duration: 1000
                  })
                }
              })
          })

        })
        .catch((err) => {
          if(err.code == '120020') {
            weex.requireModule('modal').toast({
              message:'无法获取相机和读写存储权限, 请到系统设置开启',
              duration: 1000
            })
          }else{
            weex.requireModule('modal').toast({
              message:err.message,
              duration: 1000
            })
          }
        })
    },
    addCourse () {
      let self = this
      this.isSubmitting = true
      if (this.type === 'add') {
        api.post('/edu/course/addCourse',
          {
            uid: user.userId(),
            name: this.name,
            tip: '',
            introduction: this.pictures.join(','),
            industryId: this.selectedIndustryId,
            functionId: this.selectedFunctionId,
            background: this.backGround,
            audiences: this.audiences,
            target: this.target,
            gains: this.gains,
            image: this.courseImage
          },
          function (data) {
            self.isSubmitting = false
            weex.requireModule('modal').toast({
              message: '课程添加成功',
              duration: 1000
            })
            weex.requireModule('myevent').sendGlobal('course-changed', {})
            navigator.pop()
          },
          function () {
            self.isSubmitting = false
            weex.requireModule('modal').toast({
              message: '课程添加失败',
              duration: 1000
            })
          })
      } else if (this.type === 'edit') {
        api.post('/edu/course/updateCourse',
          {
            uid: user.userId(),
            id: this.courseId,
            name: this.name,
            tip: '',
            introduction: this.pictures.join(','),
            industryId: this.selectedIndustryId,
            functionId: this.selectedFunctionId,
            background: this.backGround,
            audiences: this.audiences,
            target: this.target,
            gains: this.gains,
            image: this.courseImage
          },
          function (data) {
            self.isSubmitting = false
            weex.requireModule('modal').toast({
              message: '课程更新成功',
              duration: 1000
            })
            weex.requireModule('myevent').sendGlobal('course-changed', {})
            navigator.pop()
          },
          function () {
            self.isSubmitting = false
            weex.requireModule('modal').toast({
              message: '课程更新失败',
              duration: 1000
            })
          })
      }
    },
    deleteCourseImage () {
      this.courseImage = null
    },
    deleteDescImage (index) {
      this.pictures.splice(index, 1)
    },
    getCourseList () {
      let self = this
      api.get('/edu/course/getCourseInfoById', {id: this.courseId},
        function (data) {
          let course = data.content.course
          self.name = course.name
          self.selectedIndustryName = course.industry
          self.selectedFunctionName = course.function
          self.backGround = course.background
          self.audiences = course.audiences
          self.target = course.target
          self.gains = course.gains
          self.courseImage = course.image
          self.pictures = course.introduction ? course.introduction.split(',') : []
        },
        function () {
        })
    }
  }
}
</script>

<style scoped>
    .select-txt {
        font-size: 24px;
        color: #0f3691;
    }

    .unselect-txt {
        font-size: 24px;
        color: #333333;
    }

    .info {
        display: flex;
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
    }

    .image-delete {
        position: absolute;
        top: 0px;
        right: 0px;
        width: 40px;
        height: 40px;
    }

    .detail {
        width: 710px;
        height: 90px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        /* opacity: 0.5; */
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .detail2 {
        width: 710px;
        height: 240px;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-radius: 5px;
        background-color: rgb(255, 255, 255);
        /* opacity: 0.5; */
        flex-direction: column;
        padding-left: 20px;
        padding-right: 20px;
        padding-top: 20px;
        margin-top: 20px;
    }

    .input {
        font-size: 24px;
        height: 50px;
        color: #333333;
        flex: 1;
        margin: 20px;
        margin-left: 50px;
        margin-right: 50px;
        text-align: right;
        line-height: 50px;
    }

    .textarea {
        font-size: 24px;
        height: 50px;
        color: #333333;
        flex: 1;
        margin: 20px;
        margin-left: 50px;
    }

    .textarea {
        font-size: 24px;
        color: #333333;
        flex: 1;
        margin: 20px;
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

    .icon {
        width: 40px;
        height: 40px;
        position: absolute;
        top: 20px;
        right: 20px;
    }

    .pop-scroller {
        height: 500px;
    }

    .pop-selected {
        width: 30px;
        height: 30px;
    }

    .image-intro {
        width: 100px;
        height: 100px;
    }

    .picture-container {
        margin-top: 20px;
        width: 140px;
        height: 140px;
        align-items: center;
        justify-content: center;
    }

    .progress-div {
        flex-direction: row;
        align-items: center;
        margin-left: 30px;
        margin-top: 45px;
    }

    .progress-text {
        color: #333333;
        font-size: 30px;
        margin-left: 20px;
    }
</style>
