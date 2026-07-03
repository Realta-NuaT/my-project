import {get, post} from "@/net";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";
import router from "@/router";

export const apiUserInfo = (loadingRef) => {
    loadingRef.value = true;
    const store = useStore();
    get('/api/user/info',(data)=>{
        store.user = data
        loadingRef.value = false
    })
}
export const apiAuthRegister = (data) => {
    post('/api/auth/register',data,() => {
        ElMessage.success('注册成功,欢迎加入我们')
        router.push('/')
    })
}

export const apiAuthAskCode = (email, coldTime, timer, isEmailValid) => {
    if (!isEmailValid.value) {
        ElMessage.warning('请输入正确的电子邮件')
        return
    }
    if(timer.value){
        clearInterval(timer.value) // 避免重复点击产生多个定时器
        timer.value = null
    }
    coldTime.value = 60 // 倒计时时间
    get(`/api/auth/ask-code?email=${email}&type=register`, () => {
        ElMessage.success(`验证码已发送到邮箱: ${email}, 请注意查收`)
        // 开始倒计时
        timer.value = setInterval(() => {
            if (coldTime.value > 0) {
                coldTime.value--
            } else {
                clearInterval(timer) // 倒计时结束，清除定时器
                timer.value = null
            }
        }, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0 // 请求失败，重置倒计时
    })
}

export const apiAuthResetConform = (data, activeRef) =>
    post('api/auth/reset-confirm',data,()=>activeRef.value++)

export const apiAuthResetPassword = (data) => {
    post('api/auth/reset-password', data, ()=>{
            ElMessage.success('密码重置成功,请重新登陆')
            router.push('/')
        }
    )
}
