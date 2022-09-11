package com.example.myapplication.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SportFragmentAdapter;
import com.example.myapplication.db.Controller.CourseController;
import com.example.myapplication.db.entity.Course;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Exercise extends Fragment {
    private CourseController courseController;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //标题数组
    private String[] titles = {"运动","跑步"};

    private  List<Fragment> fragmentList;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_exercise,
                container, false);

        Log.d("tag", "onCreateView: create exercise");
        courseController = new CourseController(getContext());
        Course course = new Course();
        ArrayList<Course> res = courseController.getCourseByType("yoga");
        if (res == null){
            course = courseController.packCourse("一首歌燃脂！小马哥Cheap Thrills减肥舞蹈操，简单易上手！", "今日份快乐减脂！来自小马哥（The Fitness Marshall ）的又一大热减肥舞蹈操！  歌曲是大家熟悉的Sia - 《Cheap Thrills》~整体难度不大！前奏一响马上就能融入！一首歌的时间带你全身燃脂！大基数的新手也能轻松驾驭！一起冲！", 20, "hiit",
                    233, "111", "android.resource://com.example.myapplication/2131689473", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("超简单的5分钟瘦手臂动作，做完感觉真的很强烈！", "https://www.youtube.com/watch?v=r1FNRH-mG2E&amp;index=4&amp;list   \n" +
                            "中英字幕。公众号：瑜伽学习视频（LearningYoga），更多优质瑜伽学习视频资料", 20, "hiit",
                    289, "111", "android.resource://com.example.myapplication/2131689472", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("经典HIIT-全身高强 基础动作 杀手燃脂 紧实肌肉", "本次HIIT: -高效燃脂！超高强全身动作 训练每一块肌肉:D 心跳加速 燃烧脂肪卡路里 加速新陈代谢 -训练中没有太多花里胡哨的动作:D叫上你的男性同伴一起～  如果感觉太累需要休息 不要强迫自己休息一下再继续！就是不要放弃♥️本次训练根据身高体重燃烧70-130kcal PS: 做完本次HIIT的我充满能量:D你们也快和我一起能量超燃训练吧>3♥️", 20, "hiit",
                    669, "111", "android.resource://com.example.myapplication/2131689474", 1);
            courseController.addCourse(course);


            course = courseController.packCourse("帕梅拉一首歌4分钟全身热身运动，快和你的小伙伴一起动起来吧", "https://www.youtube.com/watch?v=wswdAzOfU1Y", 20, "pamela",
                    293, "111", "android.resource://com.example.myapplication/2131689476", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("帕梅拉 - 有氧歌曲热身|快速激活能量 提升心率 有氧燃脂", "高效 欢乐 激活全身能量的有氧歌曲热身训练来啦！只需要2分钟一首歌的时间 你就可以进行训练前的快速热身，非常适合对抗懒惰，帮助你迅速进入运动训练模式♥️ 热身运动能帮助我们提升心率 唤醒肌肉活力～ 当然 你也可以选择在运动结束后进行 燃烧更多卡路里:D 和我一起进行快速歌曲热身吧！为训练模式开启无限动力>3", 20, "pamela",
                    168, "111", "android.resource://com.example.myapplication/2131689477", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("帕梅拉 - 歌曲热身 心情加速器｜欢乐舞蹈", "踏着歌曲的节奏 欢乐跳跃起来:D 我甚至觉得我是那个 第一次去音乐节的14岁女孩～好像有一种魔力 快乐的心情就是这么简单！", 20, "pamela",
                    182, "111", "android.resource://com.example.myapplication/2131689478", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("简单快乐健身燃脂尊巴 热身最佳 新手经期友好", "把这首最适合热身的尊巴曲目 重新给大家录了横屏带口令教授的版本 音乐一响起来 不快乐都很难了!", 20, "pamela",
                    193, "111", "android.resource://com.example.myapplication/2131689479", 1);
            courseController.addCourse(course);


            course = courseController.packCourse("简单易学零基础3分钟睡前瑜伽拉伸 放松身心 提高睡眠质量！", "3分钟睡前瑜伽拉伸 放松身心 提高睡眠质量", 20, "yoga",
                    213, "111", "android.resource://com.example.myapplication/2131689483", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("2个晨起瑜伽，每天坚持10分钟，立现性感蝴蝶背", "周末时间，2个晨起动作，可以让我们元气慢慢一整天，同时让身体变柔软，保持少女身材", 20, "yoga",
                    69, "111", "android.resource://com.example.myapplication/2131689484", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("5分钟，开肩美背普拉提，越练越薄，轻松拥有美人肩背！", "宝宝们趁周末空调屋里，美背走起！3个动作，每天5分钟，开肩美背，越练越薄，轻松拥有美人肩背！感谢宝宝们的一直以来支持，爱你们！", 20, "yoga",
                    76, "111", "android.resource://com.example.myapplication/2131689485", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("休闲瑜伽·居家跟练串联 强身健体", "居家期间，要以微运动和小器械为主、瑜伽相对来说就比较舒缓和放松，居家办公一族可以再多活动肩颈和四肢的运动。今天教大家两个动作试着学习看看，帮助你缓解身体上的疲惫。", 20, "yoga",
                    239, "111", "android.resource://com.example.myapplication/2131689486", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("休闲瑜伽·居家跟练串联 身心疗愈", "身体和心灵是相互关联的和相互依存的。身体会表达心灵的想法。如果你有一颗快乐的心灵，你的脸和身体都会反映出那种快乐。居家的日常，让我们用瑜伽调节我们的身心平衡。", 20, "yoga",
                    199, "111", "android.resource://com.example.myapplication/2131689487", 1);
            courseController.addCourse(course);


            course = courseController.packCourse("5min 每日拉伸 - 运动后|睡前|清晨快速拉伸 无器械", "这是一个非常快的拉伸运动～只有5min，它可以帮助你在运动后，睡前或者是刚起床时，快速地放松拉伸身体 之前的拉伸视频都是15-30min，可能在运动后或者睡前没办法坚持～ 那么也许这个快速的拉伸能帮你形成好的缓和拉伸习惯:D 这个训练可以帮助我们拉伸身体的两侧，腿部，腹部，还可以帮助增加我们脊椎和髋关节的灵活性 让我们一起来“快速”地放松我们的身体吧", 371, "stretch",
                    199, "111", "android.resource://com.example.myapplication/2131689480", 1);
            courseController.addCourse(course);

            course = courseController.packCourse(" 5min基础每日拉伸|快速高效 运动后睡前放松全身肌肉", "全新在瑞典录制的全身基础动作拉伸～运动后/睡前/随时都可以进行的高效快速的每日拉伸训练♥️ 我知道 每次运动后还能坚持拉伸是一个艰难的决定 :D 那不如让我们用5MIN完成它！不用想和我一起做就可以！ 经典拉伸动作 帮助放松全身肌肉：包括胸腔/臀部/肩/背/大腿/腘绳肌，一些动作我们进行60秒的深度拉伸～ 希望你们享受瑞典斯德哥尔摩的美景 在音乐中和我一起深度拉伸放松自己♥️", 358, "stretch",
                    199, "111", "android.resource://com.example.myapplication/2131689481", 1);
            courseController.addCourse(course);

            course = courseController.packCourse("运动前4min无跳跃动态热身 预防受伤 高效减脂 Maroon5-Animals", "运动前不要再做静态拉伸啦 长时的静态拉伸会让肌肉放松“睡着” 不止影响运动表现 还容易增加受伤的风险 冬天运动更要注意运动前的热身 灵活关节的同时激活全身的肌肉 增加肌肉温度也可以让运动更高效 今天编的这一首适用于各种家庭运动方式 不管是舞蹈，塑型，自重的力量训练都可以~ 如果是长时间的专项运动还是要去做专项的热身哦 冬天到啦，动起来的同时更要注意安全！", 243, "stretch",
                    199, "111", "s3 android.resource://com.example.myapplication/2131689482", 1);
            courseController.addCourse(course);






            Log.i(TAG, "onCreateView: add success");
        }

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        fragmentList = new ArrayList<>();
        fragmentList.add(new SportFragment());
        fragmentList.add(new RunFragment());

        SportFragmentAdapter adapter = new SportFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentList){
            transaction.remove(fragment);
        }
        transaction.commitAllowingStateLoss();
        Log.d("tag", "onDestroyView: exercise");
    }
}
