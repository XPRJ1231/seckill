package com.ygh.service;

import com.ygh.entity.SeckillGoods;
import com.ygh.mapper.GoodsMapper;
import com.ygh.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    /**
     * 查询商品列表
     */
    public List<GoodsVO> listGoodsVO() {
        return goodsMapper.listGoodsVO();
    }

    /**
     * 根据id查询指定商品
     */
    public GoodsVO getGoodsVOByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减少库存，每次减一
     * @return 是否成功
     */
    public boolean reduceStock(GoodsVO goods) {
        int numAttempts = 0;
        int res = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        do {
            numAttempts++;
            try {
                sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
                res = goodsMapper.reduceStockByVersion(sg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res != 0) // 影响行数
                break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return res > 0;
    }
}
