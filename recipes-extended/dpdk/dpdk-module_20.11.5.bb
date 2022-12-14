include dpdk.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/dpdk:"

SRC_URI += " \
            file://0001-Makefile-add-makefile.patch \
            file://0002-kni-update-kernel-API-to-set-random-MAC-address.patch \
"
STABLE = "-stable"
BRANCH = "20.11"
SRCREV = "abf68ccc8b1f923f0eb0b6bf2a8b944378e1ebeb"
S = "${WORKDIR}/git"

inherit module

#kernel module needs 'rte_build_config.h', which is generated at buid time
DEPENDS += "dpdk"

COMPATIBLE_MACHINE = "null"
COMPATIBLE_HOST:libc-musl:class-target = "null"
COMPATIBLE_HOST:linux-gnux32 = "null"

export S
export STAGING_KERNEL_DIR
export STAGING_INCDIR
export INSTALL_MOD_DIR="dpdk"

do_configure[noexec] = "1"

do_compile() {
    cd ${S}/kernel/linux/kni
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}   \
           KERNEL_VERSION=${KERNEL_VERSION}    \
           CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
           AR="${KERNEL_AR}" \
               O=${STAGING_KERNEL_BUILDDIR} \
           KBUILD_EXTRA_SYMBOLS="${KBUILD_EXTRA_SYMBOLS}" \
           ${MAKE_TARGETS}
}

do_install() {
    cd ${S}/kernel/linux/kni
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake DEPMOD=echo MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" \
               INSTALL_FW_PATH="${D}${nonarch_base_libdir}/firmware" \
               CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
               O=${STAGING_KERNEL_BUILDDIR} \
               ${MODULES_INSTALL_TARGET}
}
