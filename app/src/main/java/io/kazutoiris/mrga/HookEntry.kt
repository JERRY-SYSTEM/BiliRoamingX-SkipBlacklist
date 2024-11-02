package io.kazutoiris.mrga

import android.content.Context
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

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
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }
}