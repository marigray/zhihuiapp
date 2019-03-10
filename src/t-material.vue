<template>
    <div>
        <nav-bar title="完善个人资料" show-back="true" @onRightTxtClicked="onNavigatorPop()"></nav-bar>
        <scroller>
            <div class='info'>
                <div class="detail">
                    <text class="unselect-txt">姓名</text>
                    <input class="input" @input="nameInput" :value="name"></input>
                    <image class="icon" :src="iconPath('icon_pen')"></image>
                </div>
                <div class="detail">
                    <text class="unselect-txt">标签</text>
                    <input class="input" @input="labelInput"
                           :value="label"></input>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail">
                    <text class="unselect-txt">国家</text>
                    <input class="input" @input="countryInput" :value="country"></input>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail">
                    <text class="unselect-txt">地区</text>
                    <input class="input" @input="cityInput" :value="city"></input>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail" @click="showLanguagePop">
                    <text class="unselect-txt">语言能力</text>
                    <text class="language-txt">{{language}}</text>
                    <image class="icon" :src="iconPath('icon_right')"></image>
                </div>
                <div class="detail2" @click="pickImage">
                    <text class="unselect-txt">请上传照片</text>
                    <image class="icon" :src="iconPath('icon_camera')"></image>
                    <image :src="headimgurl" class="image-intro"></image>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">自我介绍</text>
                    <textarea class="textarea" @input="introductionInput"
                              :value="introduction"></textarea>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">以往客户介绍</text>
                    <textarea class="textarea" @input="customerInput"
                              :value="customerIntroduction"></textarea>
                </div>
                <div class="detail2">
                    <text class="unselect-txt">请上传视频及音频介绍</text>
                    <image class="icon" :src="iconPath('icon_video')"></image>
                </div>
                <text class="experience-title">请填写从业/职业经历</text>
                <div v-if="experiences" v-for="(item, index) in experiences" :key="index">
                    <div class="detail">
                        <image class="closeIcon" :src="iconPath('icon_gb_small')"
                               @click="removeExperience(index)"></image>
                        <text class="unselect-txt">开始时间</text>
                        <input class="input" type="date" @input="experienceStartDate" :value="item.startDate"></input>
                        <image class="icon" :src="iconPath('icon_pen')"></image>
                    </div>
                    <div class="detail">
                        <text class="unselect-txt">结束时间</text>
                        <input class="input" type="date" @input="experienceEndDate" :value="item.endDate"></input>
                        <image class="icon" :src="iconPath('icon_pen')"></image>
                    </div>
                    <div class="detail2">
                        <text class="unselect-txt">从业说明：</text>
                        <textarea class="textarea" :value="item.introduction"
                                  @input="experienceIntroduction"></textarea>
                    </div>
                    <div class="list-gap"></div>
                </div>
                <div>
                    <div class="next-btn" @click="addNextExperience">
                        <text class="next-button">继续添加经历</text>
                    </div>
                </div>

            </div>
            <div class="btn" @click="addUserMaterial">
                <text style="font-size: 28px;color: #ffffff;">完成</text>
            </div>
        </scroller>

        <wxc-popup :show="isLanguagePopShow" @wxcPopupOverlayClicked="hidePop" pos="bottom" height="300">
            <scroller class="pop-scroller">
                <wxc-cell :has-top-border="false" :auto-accessible="false" v-for="item in ['中文', '英文']"
                          :title="item" @wxcCellClicked="setLanguage(item)">
                    <image class="pop-selected" v-if="item===language" :src="iconPath('icon_sus')"></image>
                </wxc-cell>
            </scroller>
        </wxc-popup>
        <zhprogress v-if="isPopupProgress" :progressValue=uploadProgressValue></zhprogress>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import zhprogress from './components/zhprogress'
import mixin from '@/modules/mixin'

import Nat from 'natjs'
import {WxcButton, WxcCell, WxcPopup} from 'weex-ui'
import api from '@/modules/api'
import user from '@/modules/user'

var navigator = weex.requireModule('navigator')
export default {
  mixins: [mixin],
  components: {NavBar, WxcCell, WxcPopup, WxcButton, zhprogress},
  data () {
    return {
      isLanguagePopShow: false,

      name: '',
      country: '',
      language: '',
      introduction: '',
      label: '',
      city: '',
      headimgurl: null,
      customerIntroduction: '',
      userInfo: {},
      userDetail: {},
      experiences: [],
      experienceItem: {},
      uploadProgressValue: 0,
      isPopupProgress: false,
      currentExperienceId:-1
    }
  },
  created () {
    this.getUserInfo()
    // this.experiences.push({uid: 1, startDate: '', endDate: '', introduction: ''})
  },
  methods: {
    showLanguagePop () {
      this.isLanguagePopShow = true
    },
    hidePop () {
      this.isLanguagePopShow = false
    },
    setLanguage (l) {
      this.language = l
    },
    nameInput (e) {
      this.name = e.value
    },
    labelInput (e) {
      this.label = e.value
    },
    countryInput (e) {
      this.country = e.value
    },
    cityInput (e) {
      this.city = e.value
    },
    introductionInput (e) {
      this.introduction = e.value
    },
    customerInput (e) {
      this.customerIntroduction = e.value
    },
    experienceStartDate (e) {
      this.experienceItem.startDate = e.value
    },
    experienceEndDate (e) {
      this.experienceItem.endDate = e.value
    },
    experienceIntroduction (e) {
      this.experienceItem.introduction = e.value
    },
    pickImage (index) {
      let self = this
      Nat.image.pick()
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
                  self.headimgurl = data.content
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
          if (err.code === '120020') {
            weex.requireModule('modal').toast({
              message: '无法获取相机和读写存储权限, 请到系统设置开启',
              duration: 1000
            })
          } else {
            weex.requireModule('modal').toast({
              message: err.message,
              duration: 1000
            })
          }
        })
    },
    addUserMaterial () {
      let self = this
      api.post('/edu/user/updateUser',
        {
          id: user.userId(),
          name: self.name,
          language: self.language,
          city: self.city,
          country: self.country,
          introduction: self.introduction,
          customerIntroduction: self.customerIntroduction,
          label: self.label,
          headimgurl: self.headimgurl
        },
        function (data) {
          self.addWorkExperience()
        },
        function () {
        })
    },
    addWorkExperience () {
      api.post1('/edu/user/addWorkExperience',
        {},
        this.experiences,
        function (data) {
          weex.requireModule('modal').toast({
            message: '资料更新成功',
            duration: 1000
          })
          navigator.pop()
        },
        function (e) {
          weex.requireModule('modal').toast({
            message: e,
            duration: 1000
          })
        })
    },
    getUserInfo () {
      let self = this
      api.get('/edu/user/getUserDetail', {id: user.userId()},
        function (data) {
          self.userInfo = data.content.userInfo
          self.name = data.content.userInfo.name
          self.label = data.content.userInfo.label
          self.country = data.content.userInfo.country
          self.city = data.content.userInfo.city
          self.language = data.content.userInfo.language
          self.headimgurl = data.content.userInfo.headimgurl
          if (data.content.userDetail.introduction !== undefined) {
            self.introduction = data.content.userDetail.introduction
          }
          if (data.content.userDetail.customerIntroduction !== undefined) {
            self.customerIntroduction = data.content.userDetail.customerIntroduction
          }
          self.experiences = data.content.workExperience
        },
        function () {

        })
    },
    addNextExperience () {
      this.currentExperienceId++
      this.experiences.push({uid: 1, startDate: '', endDate: '', introduction: ''})
    },
    removeExperience (index) {
        console.log(index)
      this.experiences.splice(index, 1);
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

    .language-txt {
        font-size: 24px;
        color: #333333;
        flex: 1;
        margin-right: 50px;
        text-align: right;
    }

    .info {
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
        /* opacity: 0.5; */
        flex-direction: row;
        align-items: center;
        padding-left: 20px;
        padding-right: 20px;
        margin-top: 20px;
    }

    .detail2 {
        width: 710px;
        height: 180px;
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
        line-height: 18px;
        color: #333333;
        flex: 1;
        margin: 20px;
        margin-left: 50px;
        margin-right: 50px;
        text-align: right;
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
        margin-top: 15px;
        margin-left: 15px;
        width: 100px;
        height: 100px;
    }

    .experience-title {
        margin-top: 15px;
        background-color: #e8eaf3;
        color: rgb(15, 54, 145);
        padding: 5px 0;
    }

    .next-btn {
        width: 710px;
        height: 90px;
        border-radius: 5px;
        margin-top: 40px;
        margin-bottom: 20px;
        justify-content: center;
        align-items: center;
        border-width: 1px;
        border-color: rgb(138, 130, 148);
        border-style: dashed;
        background-color: rgb(255, 255, 255);
    }

    .next-button {
        border-radius: 5px;
        color: rgb(15, 54, 145);
    }

    .list-gap {
        width: 710px;
        height: 5px;
        margin-top: 20px;
        background-color: #f0f0f0;
    }

    .closeIcon {
        position: absolute;
        right: -10px;
        top: -10px;
        width: 40px;
        height: 40px;
    }
</style>
