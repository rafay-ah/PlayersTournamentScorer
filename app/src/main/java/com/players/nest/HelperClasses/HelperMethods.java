package com.players.nest.HelperClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.players.nest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.players.nest.HomeFragment.PostsAdapter.IMAGE_LIKED;
import static com.players.nest.HomeFragment.PostsAdapter.IMAGE_NOT_LIKED;

public class HelperMethods {

    Context context;
    String postId;
    ImageView unLikeIcon;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<String> likeList = new ArrayList<>();

    public HelperMethods() {

    }

    public HelperMethods(Context context, String postId, ImageView unLikeIcon) {
        this.context = context;
        this.postId = postId;
        this.unLikeIcon = unLikeIcon;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.DB_POST))
                .child(postId);
    }


    public void likePostAndSaveIntoDatabase() {
        unLikeIcon.setImageResource(R.drawable.ic_liked_icon);
        unLikeIcon.clearColorFilter();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(firebaseUser.getUid(), true);
        databaseReference.child(context.getString(R.string.DB_POST_LIKES)).updateChildren(hashMap);
        unLikeIcon.setTag(IMAGE_LIKED);
    }


    public void unLikePost() {
        unLikeIcon.setImageResource(R.drawable.ic_like_icon_1);
        unLikeIcon.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.textColor, null));
        databaseReference.child(context.getString(R.string.DB_POST_LIKES)).child(firebaseUser.getUid()).removeValue();
        unLikeIcon.setTag(IMAGE_NOT_LIKED);
    }


    public void likeListener(final TextView likesCount) {

        DatabaseReference mLikesRef = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_POST))
                .child(postId).child(context.getString(R.string.DB_POST_LIKES));

        mLikesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    likeList.add(dataSnapshot.getKey());
                }
                likesCount.setText(likeList.size() + " Likes");
                if (likeList.contains(firebaseUser.getUid())) {
                    unLikeIcon.setImageResource(R.drawable.ic_liked_icon);
                    unLikeIcon.setImageTintList(null);
                    unLikeIcon.setTag(IMAGE_LIKED);
                } else {
                    unLikeIcon.setTag(IMAGE_NOT_LIKED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static SpannableStringBuilder usernameAndCaption(String username, String caption) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(username + " " + caption);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        stringBuilder.setSpan(styleSpan, 0, username.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }


    public static String getRatingStars(int ratings) {

        if (ratings >= 40 && ratings < 50)
            return Constants.HALF_STAR;
        else if (ratings >= 50 && ratings < 60)
            return Constants.ONE_STAR;
        else if (ratings >= 60 && ratings < 70)
            return Constants.TWO_STAR;
        else if (ratings >= 70 && ratings < 80)
            return Constants.TWO_N_HALF_STAR;
        else if (ratings >= 80 && ratings < 90)
            return Constants.THREE_STAR;
        else if (ratings >= 90 && ratings < 94)
            return Constants.FOUR_STAR;
        else if (ratings >= 94 && ratings < 100)
            return Constants.FOUR_N_HALF_STAR;
        else if (ratings >= 100)
            return Constants.FIVE_STAR;
        else
            return Constants.HALF_STAR;

    }


    public static void setRatings(int ratings, ImageView star1, ImageView star2, ImageView star3,
                                  ImageView star4, ImageView star5) {

        switch (getRatingStars(ratings)) {

            case Constants.ONE_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star1.setImageTintList(null);
                break;
            case Constants.TWO_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                break;
            case Constants.TWO_N_HALF_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star3.setImageResource(R.drawable.ic_half_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                star3.setImageTintList(null);
                break;
            case Constants.THREE_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star3.setImageResource(R.drawable.ic_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                star3.setImageTintList(null);
                break;
            case Constants.FOUR_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star3.setImageResource(R.drawable.ic_filled_star);
                star4.setImageResource(R.drawable.ic_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                star3.setImageTintList(null);
                star4.setImageTintList(null);
                break;
            case Constants.FOUR_N_HALF_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star3.setImageResource(R.drawable.ic_filled_star);
                star4.setImageResource(R.drawable.ic_filled_star);
                star5.setImageResource(R.drawable.ic_half_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                star3.setImageTintList(null);
                star4.setImageTintList(null);
                star5.setImageTintList(null);
                break;
            case Constants.FIVE_STAR:
                star1.setImageResource(R.drawable.ic_filled_star);
                star2.setImageResource(R.drawable.ic_filled_star);
                star3.setImageResource(R.drawable.ic_filled_star);
                star4.setImageResource(R.drawable.ic_filled_star);
                star5.setImageResource(R.drawable.ic_filled_star);
                star1.setImageTintList(null);
                star2.setImageTintList(null);
                star3.setImageTintList(null);
                star4.setImageTintList(null);
                star5.setImageTintList(null);
                break;
        }
    }


    public static String getDateFormatFromMillis(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        return simpleDateFormat.format(date);
    }


    public static String getTimeFromMillis(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }
}
