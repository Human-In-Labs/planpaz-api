-- ============================================================================
-- PlanPaz — PostgreSQL Index DDL Script (OPT-06)
-- Verified against JPA entity mappings in com.humanin.planpaz.model.*
-- ============================================================================

-- 1. Garden & Botanical Catalog Foreign Keys
CREATE INDEX IF NOT EXISTS idx_garden_plant_owner_planted
    ON public.garden_plant (owner_id, planted_at DESC);
CREATE INDEX IF NOT EXISTS idx_garden_plant_species
    ON public.garden_plant (plant_id);
CREATE INDEX IF NOT EXISTS idx_plant_stage_plant_order
    ON public.plant_stage (plant_id, "order" ASC);

-- 2. Plant Care Logs (Watering, Pruning, Fertilizing & Achievements)
CREATE INDEX IF NOT EXISTS idx_care_log_plant_type_date
    ON public.plant_care_log (garden_plant_id, care_type, performed_at DESC);
CREATE INDEX IF NOT EXISTS idx_care_log_user_type
    ON public.plant_care_log (user_id, care_type);

-- 3. Social Feed, Comments, Likes & Follows
CREATE INDEX IF NOT EXISTS idx_post_posted_at
    ON public.post (posted_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_author_posted_at
    ON public.post (author_id, posted_at DESC);
CREATE INDEX IF NOT EXISTS idx_comment_post_parent_commented
    ON public.comment (post_id, comment_id, commented_at ASC);
CREATE INDEX IF NOT EXISTS idx_comment_parent_commented
    ON public.comment (comment_id, commented_at ASC);

-- Crucial: trailing columns of composite unique constraints (author_id, post_id)
-- and (follower_id, followed_id) require standalone B-tree indexes!
CREATE INDEX IF NOT EXISTS idx_likes_post_id
    ON public.likes (post_id);
CREATE INDEX IF NOT EXISTS idx_follow_followed_id
    ON public.follow (followed_id);

-- 4. Notifications & Case-Insensitive User Search
CREATE INDEX IF NOT EXISTS idx_notifications_user_created
    ON public.notifications (user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_users_username_lower
    ON public.users (LOWER(username));
