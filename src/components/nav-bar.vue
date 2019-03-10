<template>
    <div class="bg">
        <image class="back" v-if="showBack" :src="iconPath('header_left')" @click="backClicked()"></image>
        <text class='nav-title'>{{title}}</text>
        <slot name="right-icon3" @click="rightIcon3Clicked"></slot>
        <slot name="right-icon2" @click="rightIcon2Clicked"></slot>
        <slot name="right-icon1" @click="rightIcon1Clicked"></slot>
        <!--<image class="search" v-if="showSearch" :src="iconPath('header_search')" @click="onNavigator('search')"></image>-->
        <!--<text class="nav-btn" v-if="showRightTxt" @click="onRightTxtClicked()">{{rightTxt}}</text>-->
    </div>
</template>

<script>
import mixin from '@/modules/mixin'

export default {
  props: ['title', 'showBack', 'showSearch', 'rightTxt', 'showRightTxt'],
  mixins: [mixin],
  methods: {
    backClicked () {
      weex.requireModule('navigator').pop()
    },
    rightIcon1Clicked () {
      this.$emit('rightIcon1Clicked')
    },
    rightIcon2Clicked () {
      this.$emit('rightIcon2Clicked')
    },
    rightIcon3Clicked () {
      this.$emit('rightIcon3Clicked')
    }
  }
}
</script>

<style scoped>

    .bg {
        height: 100px;
        background-color: #0f3691;
        align-items: center;
        flex-direction: row;
    }

    .back {
        width: 50px;
        height: 50px;
        margin-left: 20px;
    }

    .search {
        width: 50px;
        height: 50px;
        margin-left: 660px;
    }

    .nav-title {
        position: absolute;
        top: 0px;
        left: 100px;
        right: 100px;
        bottom: 0px;
        color: white;
        font-size: 36px;
        text-align: center;
        line-height: 100px;
    }

    .nav-btn {
        position: absolute;
        top: 0px;
        right: 20px;
        bottom: 0px;
        font-size: 24px;
        color: white;
        text-align: center;
        line-height: 100px;
    }
</style>
