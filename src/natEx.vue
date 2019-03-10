<template>
    <div>
        <nav-bar title="Title"></nav-bar>
        <div class="test" @click="pick()">
            <text class="wx-text">pick</text>
        </div>
        <div class="test" @click="upload()">
            <text class="wx-text">upload</text>
        </div>
        <div class="test" @click="uploadWithOptsnHook()">
            <text class="wx-text">uploadWithOptsnHook</text>
        </div>
        <div class="test" @click="captureImage()">
            <text class="wx-text">captureImage</text>
        </div>
        <div class="test" @click="preview()">
            <text class="wx-text">preview</text>
        </div>
        <div class="test" @click="shareWXText()">
            <text class="wx-text">shareWXText</text>
        </div>
        <div class="test" @click="shareWithEmail()">
            <text class="wx-text">shareWithEmail</text>
        </div>
    </div>
</template>

<script>
import Nat from 'natjs'
import NavBar from './components/nav-bar.vue'

export default {
  components: {NavBar},
  data () {
    return {
      url: 'http://120.26.129.194:3600/',
      filePath: '',
      name: 'Capture Image',
      path: null
    }
  },
  methods: {
    upload () {
      if (!this.filePath) {
        Nat.toast('Please pick a file first :)')
        return
      }

      Nat.upload(this.url, {
        path: this.filePath
      }, (err, res) => {
        if (err) {
          Nat.toast('[ERROR] ' + JSON.stringify(err))
          return
        }

        Nat.toast(JSON.stringify(res))
      })
    },

    uploadWithOptsnHook () {
      if (!this.filePath) {
        Nat.toast('Please pick a file first :)')
        return
      }

      Nat.upload(this.url, {
        path: this.filePath,
        formData: {
          framework: 'nat'
        },
        headers: {
          'nat-version': '0.0.1'
        }
      }, {
        onProgress: (p) => {
          Nat.toast('Progressing: ' + p)
        }
      }, (err, res) => {
        if (err) {
          Nat.toast('[ERROR] ' + JSON.stringify(err))
          return
        }

        Nat.toast(JSON.stringify(res))
      })
    },

    pick () {
      Nat.image.pick()
        .then((ret) => {
          this.filePath = ret.paths[0]
          Nat.toast('picked: ' + this.filePath)
        })
        .catch((err) => {
          // Nat.toast('[ERROR] ' + JSON.stringify(err))
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
    captureImage () {
      Nat.camera.captureImage({}, (err, ret) => {
        if (err) {
          Nat.toast('[ERROR] ' + JSON.stringify(err))
          return
        }

        Nat.toast(JSON.stringify(ret, null, 2))
        this.path = ret.path
      })
    },

    preview () {
      if (!this.path) {
        Nat.toast('Please take your photo first :)')
        return
      }

      Nat.image.preview(this.path)
    },
    shareWXText () {
      let shareInfo = {}
      shareInfo.text = '快来下载智荟APP吧！'
      shareInfo.wxscene = 2 //0 WXSceneSession; 1 WXSceneTimeline; 2 WXSceneFavorite;
      weex.requireModule('share').shareTextWX(shareInfo)
    },
    shareWithEmail () {
      Nat.mail('email@zhihui.com', {
        subject: '智荟APP邮件分享',
        body: '发现一个应用智荟APP快来下载吧！.'
      }, () => { // Add call back here.
        Nat.toast('Email popup')
      })
    }
  }
}
</script>

<style scoped>
    .btn {
        height: 100px;
        display: flex;
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
    }

    .arrow {
        margin-right: 20px;
        width: 20px;
        height: 20px;
    }

    .test {
        width: 650px;
        height: 90px;
        margin-top: 30px;
        margin-left: 50px;
        border-style: solid;
        border-width: 2px;
        border-color: rgb(111, 180, 82);
        border-radius: 3px;
        background-color: rgb(238, 240, 242);
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }

    .wx-text {
        font-size: 32px;
        color: #7ebb66;
        margin-left: 20px;
    }
</style>
