package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.engine.entity.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollisionManager {
    private final CollisionResolver resolver;
    private final List<Collidable> collidables = new ArrayList<>();

    // Tunable solver settings
    private final int maxSolverPasses;
    private final int maxPairSolverPasses;
    private final float separationEpsilon;
    private final float cellSize;

    // Last-frame diagnostics
    private FrameStats lastFrameStats = FrameStats.empty();

    public static final class FrameStats {
        private final int pairsChecked;
        private final int overlappingPairs;
        private final int pairsResolved;
        private final int separationIterations;
        private final int solverPassesUsed;

        public FrameStats(int pairsChecked, int overlappingPairs, int pairsResolved,
                          int separationIterations, int solverPassesUsed) {
            this.pairsChecked = pairsChecked;
            this.overlappingPairs = overlappingPairs;
            this.pairsResolved = pairsResolved;
            this.separationIterations = separationIterations;
            this.solverPassesUsed = solverPassesUsed;
        }

        public int getPairsChecked() { return pairsChecked; }
        public int getOverlappingPairs() { return overlappingPairs; }
        public int getPairsResolved() { return pairsResolved; }
        public int getSeparationIterations() { return separationIterations; }
        public int getSolverPassesUsed() { return solverPassesUsed; }

        private static FrameStats empty() {
            return new FrameStats(0, 0, 0, 0, 0);
        }
    }

    public CollisionManager() {
        this((a, b, contact) -> { a.onCollision(b); b.onCollision(a); }, 6, 4, 0.02f, 96f);
    }

    public CollisionManager(CollisionResolver resolver) {
        this(resolver, 6, 4, 0.02f, 96f);
    }

    public CollisionManager(CollisionResolver resolver, int maxSolverPasses, int maxPairSolverPasses, float separationEpsilon) {
        this(resolver, maxSolverPasses, maxPairSolverPasses, separationEpsilon, 96f);
    }

    public CollisionManager(CollisionResolver resolver, int maxSolverPasses, int maxPairSolverPasses, float separationEpsilon, float cellSize) {
        if (resolver == null) throw new IllegalArgumentException("CollisionResolver cannot be null");
        if (maxSolverPasses < 1) throw new IllegalArgumentException("maxSolverPasses must be >= 1");
        if (maxPairSolverPasses < 1) throw new IllegalArgumentException("maxPairSolverPasses must be >= 1");
        if (separationEpsilon < 0f) throw new IllegalArgumentException("separationEpsilon must be >= 0");
        if (cellSize <= 0f || Float.isNaN(cellSize) || Float.isInfinite(cellSize)) {
            throw new IllegalArgumentException("cellSize must be finite and > 0");
        }

        this.resolver = resolver;
        this.maxSolverPasses = maxSolverPasses;
        this.maxPairSolverPasses = maxPairSolverPasses;
        this.separationEpsilon = separationEpsilon;
        this.cellSize = cellSize;
    }

    public void addCollidable(Collidable c) {
        if (c == null || collidables.contains(c)) return;
        collidables.add(c);
    }

    public void removeCollidable(Collidable c) {
        if (c == null) return;
        collidables.remove(c);
    }

    public void update() {
        List<Collidable> snapshot = new ArrayList<>(collidables);
        Set<Long> notifiedPairs = new HashSet<Long>();

        int pairsChecked = 0;
        int overlappingPairs = 0;
        int pairsResolved = 0;
        int separationIterations = 0;
        int solverPassesUsed = 0;

        for (int pass = 0; pass < maxSolverPasses; pass++) {
            solverPassesUsed = pass + 1;
            boolean resolvedAny = false;
            Map<Long, List<Collidable>> buckets = buildBuckets(snapshot);
            Set<Long> checkedPairsThisPass = new HashSet<Long>();

            for (List<Collidable> bucket : buckets.values()) {
                for (int i = 0; i < bucket.size(); i++) {
                    for (int j = i + 1; j < bucket.size(); j++) {
                        Collidable a = bucket.get(i);
                        Collidable b = bucket.get(j);
                        if (a == null || b == null) continue;

                        long key = pairKey(a, b);
                        if (!checkedPairsThisPass.add(key)) {
                            continue;
                        }
                        if (!isCollisionEnabled(a) || !isCollisionEnabled(b)) continue;

                        pairsChecked++;

                        if ((a.getCollisionMask() & b.getCollisionLayer()) == 0 ||
                            (b.getCollisionMask() & a.getCollisionLayer()) == 0) {
                            continue;
                        }

                        CollisionContact firstContact = buildContact(a, b);
                        if (firstContact == null) continue;
                        overlappingPairs++;

                        int pairPass = 0;
                        while (pairPass < maxPairSolverPasses) {
                            CollisionContact contact = buildContact(a, b);
                            if (contact == null) break;
                            separateByContact(a, b, contact);
                            separationIterations++;
                            resolvedAny = true;
                            pairPass++;
                        }

                        if (notifiedPairs.add(key)) {
                            resolver.resolve(a, b, firstContact);
                            pairsResolved++;
                        }
                    }
                }
            }

            if (!resolvedAny) break;
        }

        lastFrameStats = new FrameStats(
            pairsChecked, overlappingPairs, pairsResolved, separationIterations, solverPassesUsed
        );
    }

    public FrameStats getLastFrameStats() {
        return lastFrameStats;
    }

    public int size() {
        return collidables.size();
    }

    public void clear() {
        collidables.clear();
    }

    public List<Collidable> getAll() {
        return Collections.unmodifiableList(collidables);
    }

    private long pairKey(Collidable a, Collidable b) {
        int ha = System.identityHashCode(a);
        int hb = System.identityHashCode(b);
        int lo = Math.min(ha, hb);
        int hi = Math.max(ha, hb);
        return (((long) lo) << 32) | (hi & 0xFFFFFFFFL);
    }

    private CollisionContact buildContact(Collidable a, Collidable b) {
        CollisionShape sa = a.getCollisionShape();
        CollisionShape sb = b.getCollisionShape();
        if (sa == null || sb == null) return null;

        CollisionShape.Type ta = sa.getType();
        CollisionShape.Type tb = sb.getType();

        if (ta == CollisionShape.Type.CIRCLE && tb == CollisionShape.Type.CIRCLE) {
            return circleCircleContact(
                (CollisionShape.CircleShape) sa,
                (CollisionShape.CircleShape) sb
            );
        } else if (ta == CollisionShape.Type.CIRCLE && tb == CollisionShape.Type.RECTANGLE) {
            return circleRectContact(
                (CollisionShape.CircleShape) sa,
                ((CollisionShape.RectShape) sb).rect
            );
        } else if (ta == CollisionShape.Type.RECTANGLE && tb == CollisionShape.Type.CIRCLE) {
            CollisionContact contact = circleRectContact(
                (CollisionShape.CircleShape) sb,
                ((CollisionShape.RectShape) sa).rect
            );
            if (contact == null) return null;
            return new CollisionContact(-contact.getNormalX(), -contact.getNormalY(), contact.getPenetration());
        } else if (ta == CollisionShape.Type.RECTANGLE && tb == CollisionShape.Type.RECTANGLE) {
            return rectRectContact(
                ((CollisionShape.RectShape) sa).rect,
                ((CollisionShape.RectShape) sb).rect
            );
        } else {
            Rectangle ra = a.getBroadPhaseBounds();
            Rectangle rb = b.getBroadPhaseBounds();
            if (ra == null || rb == null || !ra.overlaps(rb)) return null;
            return rectRectContact(ra, rb);
        }
    }

    private CollisionContact circleRectContact(CollisionShape.CircleShape circle, Rectangle rect) {
        float closestX = clamp(circle.cx, rect.x, rect.x + rect.width);
        float closestY = clamp(circle.cy, rect.y, rect.y + rect.height);
        float dx = closestX - circle.cx;
        float dy = closestY - circle.cy;
        float distSq = dx * dx + dy * dy;
        float radiusSq = circle.radius * circle.radius;
        if (distSq > radiusSq) return null;

        if (distSq > 0.0001f) {
            float dist = (float) Math.sqrt(distSq);
            float penetration = circle.radius - dist;
            return new CollisionContact(dx / dist, dy / dist, penetration);
        }

        float distLeft = circle.cx - rect.x;
        float distRight = (rect.x + rect.width) - circle.cx;
        float distBottom = circle.cy - rect.y;
        float distTop = (rect.y + rect.height) - circle.cy;

        float minDist = distLeft;
        float nx = 1f;
        float ny = 0f;

        if (distRight < minDist) {
            minDist = distRight;
            nx = -1f;
            ny = 0f;
        }
        if (distBottom < minDist) {
            minDist = distBottom;
            nx = 0f;
            ny = 1f;
        }
        if (distTop < minDist) {
            minDist = distTop;
            nx = 0f;
            ny = -1f;
        }

        return new CollisionContact(nx, ny, circle.radius + minDist);
    }

    private CollisionContact circleCircleContact(
            CollisionShape.CircleShape a,
            CollisionShape.CircleShape b) {

        float dx = b.cx - a.cx;
        float dy = b.cy - a.cy;
        float distSq = dx * dx + dy * dy;
        float radSum = a.radius + b.radius;

        if (distSq >= radSum * radSum) return null; // no overlap

        float dist = (float) Math.sqrt(distSq);
        float penetration = radSum - dist;

        float nx, ny;
        if (dist < 0.0001f) {
            nx = 1f; ny = 0f;
        } else {
            nx = dx / dist;
            ny = dy / dist;
        }
        return new CollisionContact(nx, ny, penetration);
    }

    private CollisionContact rectRectContact(Rectangle ra, Rectangle rb) {
        if (!ra.overlaps(rb)) return null;

        float aCx = ra.x + ra.width  * 0.5f,  aCy = ra.y + ra.height * 0.5f;
        float bCx = rb.x + rb.width  * 0.5f,  bCy = rb.y + rb.height * 0.5f;
        float dx = bCx - aCx,  dy = bCy - aCy;

        float overlapX = (ra.width  + rb.width)  * 0.5f - Math.abs(dx);
        float overlapY = (ra.height + rb.height) * 0.5f - Math.abs(dy);
        if (overlapX <= 0f || overlapY <= 0f) return null;

        if (overlapX < overlapY) {
            return new CollisionContact(dx >= 0f ? 1f : -1f, 0f, overlapX);
        } else {
            return new CollisionContact(0f, dy >= 0f ? 1f : -1f, overlapY);
        }
    }

    private void separateByContact(Collidable a, Collidable b, CollisionContact contact) {
        if (a.isSensor() || b.isSensor()) return;
        if (!(a instanceof PhysicalBody) || !(b instanceof PhysicalBody)) return;

        float invMassA = a.isStaticBody() ? 0f : 1f;
        float invMassB = b.isStaticBody() ? 0f : 1f;
        float sum = invMassA + invMassB;
        if (sum <= 0f) return;

        PhysicalBody ea = (PhysicalBody) a;
        PhysicalBody eb = (PhysicalBody) b;
        float sep = contact.getPenetration() + separationEpsilon;
        float nx = contact.getNormalX();
        float ny = contact.getNormalY();
        float shareA = sep * (invMassA / sum);
        float shareB = sep * (invMassB / sum);

        ea.setPosition(ea.getPositionX() - nx * shareA, ea.getPositionY() - ny * shareA);
        eb.setPosition(eb.getPositionX() + nx * shareB, eb.getPositionY() + ny * shareB);
    }

    private boolean isCollisionEnabled(Collidable collidable) {
        if (collidable instanceof Entity) {
            return ((Entity) collidable).isActive();
        }
        return true;
    }

    private Map<Long, List<Collidable>> buildBuckets(List<Collidable> snapshot) {
        Map<Long, List<Collidable>> buckets = new HashMap<>();
        for (Collidable collidable : snapshot) {
            if (collidable == null) continue;
            Rectangle bounds = collidable.getBroadPhaseBounds();
            if (bounds == null) continue;

            int minCellX = worldToCell(bounds.x);
            int maxCellX = worldToCell(bounds.x + bounds.width);
            int minCellY = worldToCell(bounds.y);
            int maxCellY = worldToCell(bounds.y + bounds.height);

            for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
                for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                    long cellKey = cellKey(cellX, cellY);
                    buckets.computeIfAbsent(cellKey, ignored -> new ArrayList<>()).add(collidable);
                }
            }
        }
        return buckets;
    }

    private int worldToCell(float coordinate) {
        return (int) Math.floor(coordinate / cellSize);
    }

    private long cellKey(int cellX, int cellY) {
        return (((long) cellX) << 32) ^ (cellY & 0xFFFFFFFFL);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
