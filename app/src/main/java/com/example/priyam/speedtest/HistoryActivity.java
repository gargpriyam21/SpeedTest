package com.example.priyam.speedtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.priyam.speedtest.HistoryDB.HistoryDBHelper;
import com.example.priyam.speedtest.HistoryDB.Models.HistoryData;
import com.example.priyam.speedtest.HistoryDB.Tables.HistoryTable;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    static ArrayList<HistoryData> history = new ArrayList<>();
    RecyclerView rvHistory;
    Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final SQLiteDatabase historyDb = new HistoryDBHelper(this).getWritableDatabase();

        rvHistory = (RecyclerView) findViewById(R.id.rvHistory);
        btnReset = (Button) findViewById(R.id.btnReset);

        final HistoryAdapter historyAdapter = new HistoryAdapter(this);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        history = HistoryTable.getHistory(historyDb);
        historyAdapter.notifyDataSetChanged();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < history.size(); i++) {
                    HistoryTable.deleteHistory(
                            historyDb,
                            history.get(i).getId()
                    );
                    history.remove(i);
                    i--;

                }
                history = HistoryTable.getHistory(historyDb);

                historyAdapter.notifyDataSetChanged();
            }
        });
    }

    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        Context context;

        public HistoryAdapter(Context context) {
            this.context = context;
        }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HistoryViewHolder(getLayoutInflater().inflate(R.layout.list_item_history, parent, false));
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            holder.bindView(history.get(position));
        }

        @Override
        public int getItemCount() {
            return history.size();
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvDown, tvDate, tvUp, tvConn;

            public HistoryViewHolder(View itemView) {
                super(itemView);
                tvDown = itemView.findViewById(R.id.tvDown);
                tvUp = itemView.findViewById(R.id.tvUp);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvConn = itemView.findViewById(R.id.tvConn);
            }

            void bindView(final HistoryData historyData) {
                tvDown.setText(historyData.getDownload());
                tvUp.setText(historyData.getUpload());
                tvDate.setText(historyData.getDate());
                tvConn.setText(historyData.getConType());
            }
        }
    }
}
