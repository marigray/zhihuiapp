<template>
    <div class="popup-container">
        <div class="mask-layer"></div>
        <div class="mask-window">
            <div class="progress-div">
                <wxc-progress :value="progressValue" bar-color='#0f3691' :bar-height=30 :bar-radius=20 :bar-width=560></wxc-progress>
                <text class="progress-text">{{progressValue}}%</text>
            </div>
        </div>
    </div>
</template>

<script>
import mixin from '@/modules/mixin'
import { WxcProgress } from 'weex-ui'

export default {
    props: ['progressValue'],
    mixins: [mixin],
    components: { WxcProgress },
    data() {
        return {
            progressValue: 0
        }
    },
    methods: {
    }
}
</script>

<style scoped>
.mask-layer {
    width: 970px;
    height: 1800px;
    background-color: #000;
    align-items: center;
    opacity: 0.5;
    flex-direction: row;
    z-index: 960;
    top: 0;
    left: 0;
    position: fixed;
}

.mask-window {
    position: fixed;
    top: 500px;
    left: 20px;
    width: 700px;
    height: 120px;
    border: 1px solid #c6c6c6;
    border-radius: 10px;
    z-index: 970;
    background-color: white;
}
.progress-div {
    flex-direction: row;
    align-items: center;
    margin-left: 30px;
    margin-top: 40px;
}
.progress-text {
    color: #333333;
    font-size: 30px;
    margin-left: 20px;
}
</style>
