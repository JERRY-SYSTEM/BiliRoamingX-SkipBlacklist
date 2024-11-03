package io.kazutoiris.mrga

import android.content.Context
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp {
            try {
                "com.bilibili.lib.blkv.internal.sp.BatchedSpImpl".toClass().method {
                    name = "getBoolean"
                }.hookAll {
                    before {
                        if (args(0).string().startsWith("user_blocked")) {
                            result = false
                        }
                    }
                }.onAllFailure { YLog.error(it.toString()) }
            } catch (ignored: Exception) {
            }

            try {
                "com.bilibili.lib.blkv.internal.sp.BatchedSpImpl".toClass().method {
                    name = "getLong"
                }.hookAll {
                    before {
                        if (args(0).string()
                                .startsWith("user_status_last_check_time")
                        ) {
                            result = Long.MAX_VALUE
                        }
                    }
                }.onAllFailure { YLog.error(it.toString()) }
            } catch (ignored: Exception) {
            }

            try {
                "android.app.ActivityThread".toClass().method {
                    name = "performLaunchActivity"
                }.hook {
                    before {
                        val app = instanceClass?.field {
                            name = "mInitialApplication"
                        }?.get(instance)?.cast<Context>()

                        appClassLoader =
                            app?.javaClass?.method {
                                name = "getClassLoader"
                            }?.get(app)?.invoke() as ClassLoader?

                        try {
                            "com.bilibili.lib.blkv.internal.sp.BatchedSpImpl".toClass().method {
                                name = "getBoolean"
                            }.hookAll {
                                before {
                                    if (args(0).string().startsWith("user_blocked")) {
                                        result = false
                                    }
                                }
                            }.onAllFailure { YLog.error(it.toString()) }
                        } catch (ignored: Exception) {
                        }

                        try {
                            "com.bilibili.lib.blkv.internal.sp.BatchedSpImpl".toClass().method {
                                name = "getLong"
                            }.hookAll {
                                before {
                                    if (args(0).string()
                                            .startsWith("user_status_last_check_time")
                                    ) {
                                        result = Long.MAX_VALUE
                                    }
                                }
                            }.onAllFailure { YLog.error(it.toString()) }
                        } catch (ignored: Exception) {
                        }

                        try {
                            "okhttp3.OkHttpClient".toClass().method {
                                name = "newCall"
                            }.hook {
                                before {
                                    val request = args(0).cast<Request>()
                                    if (request?.url?.host == "black.qimo.ink") {
                                        val newUrl = request.url.newBuilder().host("127.0.0.1").build()
                                        args(0).set(request.newBuilder().url(newUrl).build())
                                    }
                                }
                            }.onAllFailure { YLog.error(it.toString()) }
                        } catch (ignored: Exception) {
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }
}