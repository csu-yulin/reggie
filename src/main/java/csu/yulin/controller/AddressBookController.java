package csu.yulin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import csu.yulin.common.R;
import csu.yulin.config.BaseContext;
import csu.yulin.entity.AddressBook;
import csu.yulin.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getVal());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);

        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(Wrappers.lambdaUpdate(AddressBook.class)
                .eq(AddressBook::getUserId, BaseContext.getVal())
                .set(AddressBook::getIsDefault, 0));

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @CachePut
    public R<AddressBook> getDefault() {
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(Wrappers.lambdaQuery(AddressBook.class)
                .eq(AddressBook::getUserId, BaseContext.getVal())
                .eq(AddressBook::getIsDefault, 1));

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getVal());
        log.info("addressBook:{}", addressBook);
        List<AddressBook> list = addressBookService.list(Wrappers.lambdaQuery(AddressBook.class)
                .eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId())
                .orderByDesc(AddressBook::getUpdateTime));

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(list);
    }
}
