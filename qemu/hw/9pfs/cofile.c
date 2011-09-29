
/*
 * Virtio 9p backend
 *
 * Copyright IBM, Corp. 2011
 *
 * Authors:
 *  Aneesh Kumar K.V <aneesh.kumar@linux.vnet.ibm.com>
 *
 * This work is licensed under the terms of the GNU GPL, version 2.  See
 * the COPYING file in the top-level directory.
 *
 */

#include "fsdev/qemu-fsdev.h"
#include "qemu-thread.h"
#include "qemu-coroutine.h"
#include "virtio-9p-coth.h"

int v9fs_co_lstat(V9fsState *s, V9fsString *path, struct stat *stbuf)
{
    int err;

    v9fs_co_run_in_worker(
        {
            err = s->ops->lstat(&s->ctx, path->data, stbuf);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}

int v9fs_co_fstat(V9fsState *s, int fd, struct stat *stbuf)
{
    int err;

    v9fs_co_run_in_worker(
        {
            err = s->ops->fstat(&s->ctx, fd, stbuf);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}

int v9fs_co_open(V9fsState *s, V9fsFidState *fidp, int flags)
{
    int err;

    v9fs_co_run_in_worker(
        {
            fidp->fs.fd = s->ops->open(&s->ctx, fidp->path.data, flags);
            if (fidp->fs.fd == -1) {
                err = -errno;
            } else {
                err = 0;
            }
        });
    if (!err) {
        total_open_fd++;
        if (total_open_fd > open_fd_hw) {
            v9fs_reclaim_fd(s);
        }
    }
    return err;
}

int v9fs_co_open2(V9fsState *s, V9fsFidState *fidp, char *fullname, gid_t gid,
                  int flags, int mode)
{
    int err;
    FsCred cred;

    cred_init(&cred);
    cred.fc_mode = mode & 07777;
    cred.fc_uid = fidp->uid;
    cred.fc_gid = gid;
    v9fs_co_run_in_worker(
        {
            fidp->fs.fd = s->ops->open2(&s->ctx, fullname, flags, &cred);
            err = 0;
            if (fidp->fs.fd == -1) {
                err = -errno;
            }
        });
    if (!err) {
        total_open_fd++;
        if (total_open_fd > open_fd_hw) {
            v9fs_reclaim_fd(s);
        }
    }
    return err;
}

int v9fs_co_close(V9fsState *s, int fd)
{
    int err;

    v9fs_co_run_in_worker(
        {
            err = s->ops->close(&s->ctx, fd);
            if (err < 0) {
                err = -errno;
            }
        });
    if (!err) {
        total_open_fd--;
    }
    return err;
}

int v9fs_co_fsync(V9fsState *s, V9fsFidState *fidp, int datasync)
{
    int fd;
    int err;

    fd = fidp->fs.fd;
    v9fs_co_run_in_worker(
        {
            err = s->ops->fsync(&s->ctx, fd, datasync);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}

int v9fs_co_link(V9fsState *s, V9fsString *oldpath, V9fsString *newpath)
{
    int err;

    v9fs_co_run_in_worker(
        {
            err = s->ops->link(&s->ctx, oldpath->data, newpath->data);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}

int v9fs_co_pwritev(V9fsState *s, V9fsFidState *fidp,
                    struct iovec *iov, int iovcnt, int64_t offset)
{
    int fd;
    int err;

    fd = fidp->fs.fd;
    v9fs_co_run_in_worker(
        {
            err = s->ops->pwritev(&s->ctx, fd, iov, iovcnt, offset);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}

int v9fs_co_preadv(V9fsState *s, V9fsFidState *fidp,
                   struct iovec *iov, int iovcnt, int64_t offset)
{
    int fd;
    int err;

    fd = fidp->fs.fd;
    v9fs_co_run_in_worker(
        {
            err = s->ops->preadv(&s->ctx, fd, iov, iovcnt, offset);
            if (err < 0) {
                err = -errno;
            }
        });
    return err;
}
