package com.example.exe201.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.exe201.R;

import java.util.List;

public class TypeSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;
    private boolean[] selectedItems;

    public TypeSpinnerAdapter(Context context, List<String> items, boolean[] selectedItems) {
        this.context = context;
        this.items = items;
        this.selectedItems = selectedItems;
    }

    @Override
    public int getCount() {
        return items.size(); // Số lượng item trong Spinner
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); // Trả về item tại vị trí `position`
    }

    @Override
    public long getItemId(int position) {
        return position; // Trả về ID của item, thường là vị trí
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_with_check, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.spinner_item_text);
        CheckBox checkBox = convertView.findViewById(R.id.check_box);

        String item = items.get(position);
        textView.setText(item);

        // Tránh thiết lập lại quá nhiều sự kiện cho CheckBox
        checkBox.setOnCheckedChangeListener(null);
        // Thiết lập trạng thái của CheckBox dựa trên mảng selectedItems
        checkBox.setChecked(selectedItems[position]);
        // Log thông tin vị trí và trạng thái của CheckBox
        Log.d("Adapter", "Position: " + position + " - Selected: " + selectedItems[position]);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Cập nhật trạng thái đã chọn trong mảng selectedItems
                selectedItems[position] = isChecked;
            }
        });

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Tương tự như `getView`, nhưng có thể tùy chỉnh cho dropdown
        return getView(position, convertView, parent);
    }

    // Phương thức trả về trạng thái các mục đã chọn
    public boolean[] getSelectedItems() {
        return selectedItems;
    }
}
