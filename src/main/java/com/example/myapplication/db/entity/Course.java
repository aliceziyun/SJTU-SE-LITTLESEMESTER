package com.example.myapplication.db.entity;

public class Course{
    int id;
    String courseName;
    String description;
    int kcal;
    String type;
    int duration;
    String imgUrl;
    String srcUrl;
    int isFavored;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDuration(int duration) { this.duration = duration; }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setFavored(int favored) { isFavored = favored; }

    public void setId(int id) { this.id = id; }


    public int getKcal() {
        return kcal;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getDuration() { return duration; }

    public String getSrcUrl() {
        return srcUrl;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int isFavored() { return isFavored; }



    //获取视频显示卡
//    public LinearLayout getLayoutCard(Context context){
//        LinearLayout layout = new LinearLayout(context);
//        LinearLayout rightLayout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(10, 20, 10, 15);
//        layout.setLayoutParams(params);
//
//
//        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(R.drawable.ic_launcher_background);
//        imageView.setPaddingRelative(20, 5, 20, 5);
//        layout.addView(imageView);
//
//
//        rightLayout.setOrientation(LinearLayout.VERTICAL);
//        LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        paramsRight.setMargins(50, 10, 0, 0);
//        rightLayout.setLayoutParams(paramsRight);
//
//        TextView tvTitle = new TextView(context);
//        tvTitle.setText(courseName);
//        tvTitle.setTextSize(25);
//        TextPaint paint = tvTitle.getPaint();
//        paint.setFakeBoldText(true);
//
//        TextView tvDescirption = new TextView(context);
//        tvDescirption.setText(description);
//        tvDescirption.setTextSize(15);
//
//        TextView tvLength = new TextView(context);
//        tvLength.setText("时长：" + length);
//        tvLength.setTextSize(17);
//
//        rightLayout.addView(tvTitle);
//        rightLayout.addView(tvLength);
//        rightLayout.addView(tvDescirption);
//
//        layout.addView(rightLayout);
//
//        return layout;
//    }
}
