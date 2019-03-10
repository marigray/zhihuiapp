<template>
    <div class="wrapper">
        <nav-bar show-back="true" title="课程订单"></nav-bar>
        <div class='tag-view'>
            <div class='tag-btn' v-for="(item,index) in taglist" :key="index" @click="onTabClicked(index)">
                <text :class="['tag-text', tabIndex==index?'tag-text-selected':'']">{{item}}</text>
                <div :class="['tag-line', tabIndex==index?'tag-line-selected':'']"></div>
            </div>
        </div>

        <slider class="slider" :style="{height:height+'px'}" auto-play="false" :index="tabIndex" @change="onTabChanged">
            <order-list status="0" class="list" :height="sliderHeight"></order-list>
            <order-list status="1" class="list" :height="sliderHeight"></order-list>
            <order-list status="4" class="list" :height="sliderHeight"></order-list>
        </slider>
    </div>
</template>

<script>
import OrderList from './order-list'
import NavBar from './components/nav-bar.vue'
import mixin from '@/modules/mixin'

export default {
  mixins: [mixin],
  components: {NavBar, OrderList},
  data () {
    return {
      tabIndex: 0,
      taglist: ['待付款', '已付款', '已取消']
    }
  },
  computed: {
    sliderHeight () {
      return this.deviceHeight - 160
    }
  },
  created () {
  },
  methods: {
    onTabClicked (i) {
      this.tabIndex = i
    },
    onTabChanged (e) {
      this.tabIndex = e.index
    }
  }
}
</script>

<style scoped>

    .wrapper {
        flex: 1;
    }

    .slider {
        width: 750px;
    }

    .list {
        width: 750px;
        background-color: rgb(249, 249, 249);
    }

    .tag-view {
        width: 750px;
        height: 80px;
        background-color: rgb(245, 245, 245);
        display: flex;
        flex-direction: row;
    }

    .tag-btn {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: center;
        flex-direction: column;
    }

    .tag-text {
        margin-top: 20px;
        font-size: 28px;
    }

    .tag-text-selected {
        color: #0f3691;
    }

    .tag-line {
        margin-top: 20px;
        width: 80px;
        height: 5px;
        background-color: #003797;
        opacity: 0;
    }

    .tag-line-selected {
        opacity: 1;
    }

    /* cell */

    .cell1 {
        padding: 20px;
        background-color: rgb(249, 249, 249);
        border-radius: 1px;
    }

    .cell1-content {
        background-color: white;
        border-radius: 10px;
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
        height: 220px;
        flex-direction: row;
    }

    .cell1-bottom-right {
        display: flex;
        margin-top: 20px;
        margin-bottom: 20px;
        flex: 1;
        flex-direction: column;
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
        padding: 5px;
        font-size: 20px;
        color: white;
        width: 60px;
        text-align: center;
        border-radius: 5px;
        background-color: #0f3691;
    }

    .cell1-price {
        font-size: 28px;
        color: #ec5f59;
    }

    .pay-container {
        display: flex;
        flex-direction: row;
        height: 80px;
        align-items: center;
        justify-content: flex-end;
    }

    .pay-line {
        width: 710px;
        height: 1px;
        background-color: #f0f0f0;
    }

    .cancel-btn,
    .pay-btn {
        font-size: 24px;
        margin-right: 20px;
        border-style: solid;
        border-width: 1px;
        border-color: rgb(150, 150, 150);
        width: 140px;
        text-align: center;
        border-radius: 30px;
        padding-top: 10px;
        padding-bottom: 10px;
    }

    .pay-btn {
        color: #ec5f59;
        border-color: #ec5f59;
    }
</style>
