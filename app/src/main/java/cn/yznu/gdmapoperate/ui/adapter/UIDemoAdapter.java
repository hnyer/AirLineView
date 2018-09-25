package cn.yznu.gdmapoperate.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mvp.lt.airlineview.R;

import java.util.List;

import cn.yznu.gdmapoperate.DemoModel;


public class UIDemoAdapter extends BaseQuickAdapter<DemoModel, BaseViewHolder> {
    public UIDemoAdapter(@Nullable List<DemoModel> data) {
        super(R.layout.adapter_uidemo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DemoModel item) {
        helper.setText(R.id.txt_title, item.getTitle());
    }
}
