
package net.nashlegend.quickid.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.nashlegend.quickid.AppApplication;
import net.nashlegend.quickid.model.Contact;
import net.nashlegend.quickid.model.Contact.PointPair;
import net.nashlegend.quickid.model.Contact.ScoreAndHits;
import net.nashlegend.quickid.view.ContactView;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class ContactAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private Context mContext;
    private int display_Mode = ContactView.Display_Mode_Display;

    public ContactAdapter(Context context, int display) {
        mContext = context;
        display_Mode = display;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            ContactView contactView = new ContactView(mContext, display_Mode);
            holder.contactView = contactView;
            contactView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.contactView.setContact(contacts.get(position));
            holder.contactView.build();
        } catch (Exception e) {
            
        }

        return holder.contactView;
    }

    class ViewHolder {
        ContactView contactView;
    }

    public void sortContact(ArrayList<Contact> lis) {
        ContactComparator comparator = new ContactComparator();
        Collections.sort(lis, comparator);
    }

    public class ContactComparator implements Comparator<Contact> {

        @Override
        public int compare(Contact lhs, Contact rhs) {

            if (lhs.matchValue.score > rhs.matchValue.score) {
                return -1;
            } else if (lhs.matchValue.score == rhs.matchValue.score) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private String preQueryString = "";

    private Filter filter = new Filter() {
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            if (results != null) {
                setContacts((ArrayList<Contact>) results.values);
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }

        @Override
        synchronized protected FilterResults performFiltering(CharSequence constraint) {
            // 手速过快的话，有可能在执行这里的时候正在执行getView，这时候却修改了contact的内容，有可能报错
            // 所以这时候有三种方法解决这个问题。
            // 一是同步getView和performFiltering方法，让他们不相互打断，这很难实现，得重新实现adapter,listView
            // 二是执行performFiltering不修改contacts列表，这就要求使用contacts列表的一个clone，但是这样效率低下
            // 三是仍然允许performFiltering方法修改contacts内容，但是要在getView方法里做好预案
            // 当发现数据已经变得有问题的时候，直接返回不做处理，而当performFiltering执行完毕后再执行publishResults后。
            // 联系人列表将迅速发生改变，这样肉眼无法识别其实有那么20毫秒的时候里有几个联系人的匹配内容显示有问题。
            // 第三种方法要求performFiltering使用synchronized，并且setContacts(resultList)要写在此方法中

            // 2014-09-29 11:01:11 update
            // 上一次修改只处理了修改了单个contact的问题，但是还有另一个问题：setContacts();之后并没有立即notifyDataSetChanged();
            // 在notifyDataSetChanged之后，adapter会顺序执行getView，但是在getView的时候，setContacts可能又会执行，
            // 从而改变了contacts的长度,contacts.get(position)可能会发生越界的问题，因此这时候getView要捕获这个错误
            // 返回一个空view，跟上次一样，空view存在时间很短，不会有人注意的……
            if (TextUtils.isEmpty(constraint)
                    || preQueryString.equals(constraint)) {
                return null;
            }

            String queryString = constraint.toString();
            FilterResults results = new FilterResults();
            int preLength = preQueryString.length();
            int queryLength = queryString.length();
            ArrayList<Contact> baseList = new ArrayList<Contact>();
            ArrayList<Contact> resultList = new ArrayList<Contact>();
            // 点击过快的话，第一个publishResults还没执行到，第二个performFiltering就已经开始了，
            // 如果使用contacts做baseList的话有可能导致搜索不到。
            // 就算是使用AllContacts做baseList基本没有问题，Nexus5 270联系人搜索不超过10ms
            
            // if (preLength > 0 && (preLength == queryLength - 1)
            // && queryString.startsWith(preQueryString)) {
            // baseList = contacts;
            // } else {
            // baseList = AppApplication.AllContacts;
            // }
            
            baseList = AppApplication.AllContacts;
            for (Iterator<Contact> iterator = baseList.iterator(); iterator
                    .hasNext();) {
                Contact contact = (Contact) iterator.next();
                if (contact.match(queryString) > 0) {
                    resultList.add(contact);
                }
            }
            sortContact(resultList);
            preQueryString = queryString;
            results.values = resultList;
            results.count = resultList.size();
            return results;
        }
    };
}
