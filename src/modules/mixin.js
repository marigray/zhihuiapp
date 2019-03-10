import user from '@/modules/user'
import api from '@/modules/api'

var navigator = weex.requireModule('navigator')

export default {
  data: function () {
    return {}
  },
  computed: {
    deviceHeight: {
      cache: false,
      get: function () {
        return weex.config.env.deviceHeight / weex.config.env.deviceWidth * 750
      }
    }
  },
  methods: {
    isClientStudent () {
      return weex.config.env.scheme === 'zhihui'
    },
    iconPath (name) {
      return 'http://adminblog.izlong.com/xxx/' + name + '.png'
    },
    formatDate (timestamp, format) {
      if (!format) format = 'Y-m-d H:i'
      const date = new Date(timestamp * 1000)
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      const day = date.getDate()
      const hour = date.getHours()
      const minute = date.getMinutes()
      const second = date.getSeconds()
      let result = format.replace('Y', year)
      result = result.replace('m', (month < 10 ? '0' + month : month))
      result = result.replace('d', (day < 10 ? '0' + day : day))
      result = result.replace('H', (hour < 10 ? '0' + hour : hour))
      result = result.replace('i', (minute < 10 ? '0' + minute : minute))
      result = result.replace('s', (second < 10 ? '0' + second : second))
      return result
    },
    CourseTypeString (type) {
      if (type === 1) {
        return '线上'
      } else if (type === 2) {
        return '线下'
      } else if (type === 3) {
        return '专栏'
      }
      return ''
    },
    isLogined () {
      // if (user.userId() === '0' || user.userId() === 0) {
      //   return false
      // }
      return true
    },
    pushLogin () {
      navigator.push({
        url: api.getUri('login'),
        animated: true
      })
    }
  }
}
