export default {
  userId () {
    return weex.requireModule('user').getUserid()
  }
}
