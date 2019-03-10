<template>
    <div>
        <nav-bar title="添加背景" show-back="true"></nav-bar>

        <waterfall class='list' show-scrollbar='false' column-with='auto' column-count=3 column-gap=12 left-gap=20
                   right-gap=20>
            <cell class="cell" v-for="(item,index) in imgList" :key="index">
                <image class="cell-background-img" :src="item.image"></image>
            </cell>
        </waterfall>

        <div class="btn-upload" @click="pick()">
            <image class="photo" :src="iconPath('add_photo')"></image>
            <text style="font-size: 32px;color: #0f3169;">上传我的图片</text>
        </div>

        <div class="btn" @click="onNavigatorPop()">
            <text style="font-size: 32px;color: #ffffff;">确定</text>
        </div>

    </div>
</template>

<script>
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'
import Nat from 'natjs'

var navigator = weex.requireModule('navigator')

export default {
  mixins: [mixin],
  components: {NavBar},
  data () {
    return {
      imgList: [
        {image: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537520038561&di=d26f4d72d4756cb3433841c14889a15d&imgtype=0&src=http%3A%2F%2Fs6.sinaimg.cn%2Fmw690%2F001ZDImLgy6QQQSA709e5%26690'},
        {image: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537520038561&di=d26f4d72d4756cb3433841c14889a15d&imgtype=0&src=http%3A%2F%2Fs6.sinaimg.cn%2Fmw690%2F001ZDImLgy6QQQSA709e5%26690'},
        {image: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537520038561&di=d26f4d72d4756cb3433841c14889a15d&imgtype=0&src=http%3A%2F%2Fs6.sinaimg.cn%2Fmw690%2F001ZDImLgy6QQQSA709e5%26690'},
        {image: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1537520038561&di=d26f4d72d4756cb3433841c14889a15d&imgtype=0&src=http%3A%2F%2Fs6.sinaimg.cn%2Fmw690%2F001ZDImLgy6QQQSA709e5%26690'}
      ]
    }
  },
  methods: {
    onNavigatorPop () {
      navigator.pop()
    },
    pick () {
      Nat.image.pick()
        .then((ret) => {
          this.filePath = ret.paths[0]
          Nat.toast('picked: ' + this.filePath)
        })
        .catch((err) => {
        //   Nat.toast('[ERROR] ' + JSON.stringify(err))
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
    }
  }
}
</script>

<style scoped>
    .list {
        flex: 1;
        background-color: #ffffff;
    }

    .cell-background-img {
        width: 220px;
        height: 180px;
        margin-top: 30px;
        border-radius: 10px;
        background-color: #d2d2d2;
    }

    .photo {
        width: 30px;
        height: 30px;
        margin-right: 20px;
    }

    .btn-upload {
        margin-top: 50px;
        margin-left: 20px;
        width: 710px;
        height: 90px;
        border-radius: 5px;
        border-color: #0f3169;
        border-width: 2px;
        background-color: #ffffff;
        flex-direction: row;
        justify-content: center;
        align-items: center;
    }

    .btn {
        margin-top: 20px;
        margin-bottom: 20px;
        margin-left: 20px;
        width: 710px;
        height: 90px;
        border-radius: 5px;
        background-color: rgb(15, 54, 145);
        justify-content: center;
        align-items: center;
        color: #ffffff;
    }
</style>
