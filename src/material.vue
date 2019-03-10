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
                <!--<div class="detail">-->
                <!--<text class="unselect-txt">邮箱</text>-->
                <!--<input class="input" @input="emailInput" :value="userInfo.email"></input>-->
                <!--<image class="icon" :src="iconPath('icon_pen')"></image>-->
                <!--</div>-->
                <!--<div class="detail">-->
                <!--<text class="unselect-txt">联系电话</text>-->
                <!--<input class="input" @input="teleInput" :value="userInfo.phone"></input>-->
                <!--<image class="icon" :src="iconPath('icon_pen')"></image>-->
                <!--</div>-->
                <!--<div class="detail">-->
                <!--<text class="unselect-txt">选择地区</text>-->
                <!--<input class="input" @input="areaInput" :value="userInfo.city"></input>-->
                <!--<image class="icon" :src="iconPath('icon_right')"></image>-->
                <!--</div>-->
                <!--<div class="detail">-->
                <!--<text class="unselect-txt">语言能力</text>-->
                <!--<input class="input" @input="languageInput" :value="userInfo.language"></input>-->
                <!--<image class="icon" :src="iconPath('icon_right')"></image>-->
                <!--</div>-->
                <div class="detail2" @click="pickImage">
                    <text class="unselect-txt">请上传照片</text>
                    <image class="icon" :src="iconPath('icon_camera')"></image>
                    <image :src="headimgurl" class="image-intro"></image>
                </div>
                <!--<div class="detail2">-->
                <!--<text class="unselect-txt">自我介绍</text>-->
                <!--<textarea class="textarea" @input="describeInput"></textarea>-->
                <!--</div>-->
                <!--<div class="detail2">-->
                <!--<text class="unselect-txt">请上传视频及音频介绍</text>-->
                <!--<image class="icon" :src="iconPath('icon_video')"></image>-->
                <!--</div>-->
            </div>
            <div class="btn" @click="addStudentMaterial">
                <text style="font-size: 28px;color: #ffffff;">完成</text>
            </div>
        </scroller>
        <zhprogress v-if="isPopupProgress" :progressValue=uploadProgressValue></zhprogress>
    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import zhprogress from './components/zhprogress'
import mixin from '@/modules/mixin'
import user from '@/modules/user'
import api from '@/modules/api'
import Nat from 'natjs'

var navigator = weex.requireModule('navigator')
export default {
  mixins: [mixin],
  components: {NavBar, zhprogress},
  data () {
    return {
      headimgurl: null,
      name: '',
      email: '',
      tele: '',
      area: '',
      language: '',
      userInfo: {},
      userDetail: {},
      uploadProgressValue: 0,
      isPopupProgress: false
    }
  },
  created () {
    this.getStudentDetail()
  },
  methods: {
    nameInput (e) {
      this.name = e.value
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
          if (err.code == '120020') {
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
    addStudentMaterial () {
      api.post('/edu/user/updateUser',
        {
          id: user.userId(),
          name: this.name,
          headimgurl: this.headimgurl
        },
        function (data) {
          weex.requireModule('modal').toast({
            message: '资料更新成功',
            duration: 1000
          })
          navigator.pop()
        },
        function () {
        })
    },
    getStudentDetail () {
      let self = this
      api.get('/edu/user/getUserDetail', {id: user.userId()},
        function (data) {
          self.name = data.content.userInfo.name
          self.headimgurl = data.content.userInfo.headimgurl
        },
        function () {

        })
    }
  }
}
</script>

<style scoped>
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

    .image-intro {
        margin-top: 15px;
        margin-left: 15px;
        width: 100px;
        height: 100px;
    }

</style>
