package com.openfeint.internal.vendor.org.codehaus.jackson.sym;

import com.openfeint.internal.vendor.org.codehaus.jackson.util.InternCache;
import com.sponsorpay.sdk.android.publisher.SponsorPayPublisher;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class BytesToNameCanonicalizer {
    protected static final int DEFAULT_TABLE_SIZE = 64;
    static final int INITIAL_COLLISION_LEN = 32;
    static final int LAST_VALID_BUCKET = 254;
    static final int MAX_ENTRIES_FOR_REUSE = 6000;
    protected static final int MAX_TABLE_SIZE = 65536;
    static final int MIN_HASH_SIZE = 16;
    private int _collCount;
    private int _collEnd;
    private Bucket[] _collList;
    private boolean _collListShared;
    private int _count;
    final boolean _intern;
    private int[] _mainHash;
    private int _mainHashMask;
    private boolean _mainHashShared;
    private Name[] _mainNames;
    private boolean _mainNamesShared;
    private transient boolean _needRehash;
    final BytesToNameCanonicalizer _parent;

    public static BytesToNameCanonicalizer createRoot() {
        return new BytesToNameCanonicalizer(DEFAULT_TABLE_SIZE, true);
    }

    public synchronized BytesToNameCanonicalizer makeChild(boolean canonicalize, boolean intern) {
        return new BytesToNameCanonicalizer(this, intern);
    }

    public void release() {
        if (maybeDirty() && this._parent != null) {
            this._parent.mergeChild(this);
            markAsShared();
        }
    }

    private BytesToNameCanonicalizer(int hashSize, boolean intern) {
        this._parent = null;
        this._intern = intern;
        if (hashSize < MIN_HASH_SIZE) {
            hashSize = MIN_HASH_SIZE;
        } else if (((hashSize - 1) & hashSize) != 0) {
            int curr = MIN_HASH_SIZE;
            while (curr < hashSize) {
                curr += curr;
            }
            hashSize = curr;
        }
        initTables(hashSize);
    }

    private BytesToNameCanonicalizer(BytesToNameCanonicalizer parent, boolean intern) {
        this._parent = parent;
        this._intern = intern;
        this._count = parent._count;
        this._mainHashMask = parent._mainHashMask;
        this._mainHash = parent._mainHash;
        this._mainNames = parent._mainNames;
        this._collList = parent._collList;
        this._collCount = parent._collCount;
        this._collEnd = parent._collEnd;
        this._needRehash = false;
        this._mainHashShared = true;
        this._mainNamesShared = true;
        this._collListShared = true;
    }

    private void initTables(int hashSize) {
        this._count = 0;
        this._mainHash = new int[hashSize];
        this._mainNames = new Name[hashSize];
        this._mainHashShared = false;
        this._mainNamesShared = false;
        this._mainHashMask = hashSize - 1;
        this._collListShared = true;
        this._collList = null;
        this._collEnd = 0;
        this._needRehash = false;
    }

    private synchronized void mergeChild(BytesToNameCanonicalizer child) {
        int childCount = child._count;
        if (childCount > this._count) {
            if (child.size() > MAX_ENTRIES_FOR_REUSE) {
                initTables(DEFAULT_TABLE_SIZE);
            } else {
                this._count = child._count;
                this._mainHash = child._mainHash;
                this._mainNames = child._mainNames;
                this._mainHashShared = true;
                this._mainNamesShared = true;
                this._mainHashMask = child._mainHashMask;
                this._collList = child._collList;
                this._collCount = child._collCount;
                this._collEnd = child._collEnd;
            }
        }
    }

    private void markAsShared() {
        this._mainHashShared = true;
        this._mainNamesShared = true;
        this._collListShared = true;
    }

    public int size() {
        return this._count;
    }

    public boolean maybeDirty() {
        return !this._mainHashShared;
    }

    public static Name getEmptyName() {
        return Name1.getEmptyName();
    }

    public Name findName(int firstQuad) {
        int hash = calcHash(firstQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, 0);
            }
        }
        return null;
    }

    public Name findName(int firstQuad, int secondQuad) {
        int hash = calcHash(firstQuad, secondQuad);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null) {
                return null;
            }
            if (name.equals(firstQuad, secondQuad)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, firstQuad, secondQuad);
            }
        }
        return null;
    }

    public Name findName(int[] quads, int qlen) {
        int hash = calcHash(quads, qlen);
        int ix = hash & this._mainHashMask;
        int val = this._mainHash[ix];
        if ((((val >> 8) ^ hash) << 8) == 0) {
            Name name = this._mainNames[ix];
            if (name == null || name.equals(quads, qlen)) {
                return name;
            }
        } else if (val == 0) {
            return null;
        }
        int val2 = val & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE;
        if (val2 > 0) {
            Bucket bucket = this._collList[val2 - 1];
            if (bucket != null) {
                return bucket.find(hash, quads, qlen);
            }
        }
        return null;
    }

    public Name addName(String symbolStr, int[] quads, int qlen) {
        if (this._intern) {
            symbolStr = InternCache.instance.intern(symbolStr);
        }
        int hash = calcHash(quads, qlen);
        Name symbol = constructName(hash, symbolStr, quads, qlen);
        _addSymbol(hash, symbol);
        return symbol;
    }

    public static final int calcHash(int firstQuad) {
        int hash = firstQuad ^ (firstQuad >>> MIN_HASH_SIZE);
        return hash ^ (hash >>> 8);
    }

    public static final int calcHash(int firstQuad, int secondQuad) {
        int hash = (firstQuad * 31) + secondQuad;
        int hash2 = hash ^ (hash >>> MIN_HASH_SIZE);
        return hash2 ^ (hash2 >>> 8);
    }

    public static final int calcHash(int[] quads, int qlen) {
        int hash = quads[0];
        for (int i = 1; i < qlen; i++) {
            hash = (hash * 31) + quads[i];
        }
        int hash2 = hash ^ (hash >>> MIN_HASH_SIZE);
        return hash2 ^ (hash2 >>> 8);
    }

    private void _addSymbol(int hash, Name symbol) {
        int bucket;
        if (this._mainHashShared) {
            unshareMain();
        }
        if (this._needRehash) {
            rehash();
        }
        this._count++;
        int ix = hash & this._mainHashMask;
        if (this._mainNames[ix] == null) {
            this._mainHash[ix] = hash << 8;
            if (this._mainNamesShared) {
                unshareNames();
            }
            this._mainNames[ix] = symbol;
        } else {
            if (this._collListShared) {
                unshareCollision();
            }
            this._collCount++;
            int entryValue = this._mainHash[ix];
            int bucket2 = entryValue & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE;
            if (bucket2 == 0) {
                if (this._collEnd <= 254) {
                    bucket = this._collEnd;
                    this._collEnd++;
                    if (bucket >= this._collList.length) {
                        expandCollision();
                    }
                } else {
                    bucket = findBestBucket();
                }
                this._mainHash[ix] = (entryValue & (-256)) | (bucket + 1);
            } else {
                bucket = bucket2 - 1;
            }
            this._collList[bucket] = new Bucket(symbol, this._collList[bucket]);
        }
        int hashSize = this._mainHash.length;
        if (this._count > (hashSize >> 1)) {
            int hashQuarter = hashSize >> 2;
            if (this._count > hashSize - hashQuarter) {
                this._needRehash = true;
            } else if (this._collCount >= hashQuarter) {
                this._needRehash = true;
            }
        }
    }

    private void rehash() {
        int bucket;
        this._needRehash = false;
        this._mainNamesShared = false;
        int[] oldMainHash = this._mainHash;
        int len = oldMainHash.length;
        int newLen = len + len;
        if (newLen > MAX_TABLE_SIZE) {
            nukeSymbols();
            return;
        }
        this._mainHash = new int[newLen];
        this._mainHashMask = newLen - 1;
        Name[] oldNames = this._mainNames;
        this._mainNames = new Name[newLen];
        int symbolsSeen = 0;
        for (int i = 0; i < len; i++) {
            Name symbol = oldNames[i];
            if (symbol != null) {
                symbolsSeen++;
                int hash = symbol.hashCode();
                int ix = hash & this._mainHashMask;
                this._mainNames[ix] = symbol;
                this._mainHash[ix] = hash << 8;
            }
        }
        int oldEnd = this._collEnd;
        if (oldEnd != 0) {
            this._collCount = 0;
            this._collEnd = 0;
            this._collListShared = false;
            Bucket[] oldBuckets = this._collList;
            this._collList = new Bucket[oldBuckets.length];
            for (int i2 = 0; i2 < oldEnd; i2++) {
                for (Bucket curr = oldBuckets[i2]; curr != null; curr = curr.mNext) {
                    symbolsSeen++;
                    Name symbol2 = curr.mName;
                    int hash2 = symbol2.hashCode();
                    int ix2 = hash2 & this._mainHashMask;
                    int val = this._mainHash[ix2];
                    if (this._mainNames[ix2] == null) {
                        this._mainHash[ix2] = hash2 << 8;
                        this._mainNames[ix2] = symbol2;
                    } else {
                        this._collCount++;
                        int bucket2 = val & SponsorPayPublisher.DEFAULT_OFFERWALL_REQUEST_CODE;
                        if (bucket2 == 0) {
                            if (this._collEnd <= 254) {
                                bucket = this._collEnd;
                                this._collEnd++;
                                if (bucket >= this._collList.length) {
                                    expandCollision();
                                }
                            } else {
                                bucket = findBestBucket();
                            }
                            this._mainHash[ix2] = (val & (-256)) | (bucket + 1);
                        } else {
                            bucket = bucket2 - 1;
                        }
                        this._collList[bucket] = new Bucket(symbol2, this._collList[bucket]);
                    }
                }
            }
            if (symbolsSeen != this._count) {
                throw new RuntimeException("Internal error: count after rehash " + symbolsSeen + "; should be " + this._count);
            }
        }
    }

    private void nukeSymbols() {
        this._count = 0;
        Arrays.fill(this._mainHash, 0);
        Arrays.fill(this._mainNames, (Object) null);
        Arrays.fill(this._collList, (Object) null);
        this._collCount = 0;
        this._collEnd = 0;
    }

    private int findBestBucket() {
        Bucket[] buckets = this._collList;
        int bestCount = Integer.MAX_VALUE;
        int bestIx = -1;
        int len = this._collEnd;
        for (int i = 0; i < len; i++) {
            int count = buckets[i].length();
            if (count < bestCount) {
                if (count != 1) {
                    bestCount = count;
                    bestIx = i;
                } else {
                    return i;
                }
            }
        }
        int i2 = bestIx;
        return i2;
    }

    private void unshareMain() {
        int[] old = this._mainHash;
        int len = this._mainHash.length;
        this._mainHash = new int[len];
        System.arraycopy(old, 0, this._mainHash, 0, len);
        this._mainHashShared = false;
    }

    private void unshareCollision() {
        Bucket[] old = this._collList;
        if (old == null) {
            this._collList = new Bucket[INITIAL_COLLISION_LEN];
        } else {
            int len = old.length;
            this._collList = new Bucket[len];
            System.arraycopy(old, 0, this._collList, 0, len);
        }
        this._collListShared = false;
    }

    private void unshareNames() {
        Name[] old = this._mainNames;
        int len = old.length;
        this._mainNames = new Name[len];
        System.arraycopy(old, 0, this._mainNames, 0, len);
        this._mainNamesShared = false;
    }

    private void expandCollision() {
        Bucket[] old = this._collList;
        int len = old.length;
        this._collList = new Bucket[len + len];
        System.arraycopy(old, 0, this._collList, 0, len);
    }

    private static Name constructName(int hash, String name, int[] quads, int qlen) {
        if (qlen < 4) {
            switch (qlen) {
                case 1:
                    return new Name1(name, hash, quads[0]);
                case 2:
                    return new Name2(name, hash, quads[0], quads[1]);
                case 3:
                    return new Name3(name, hash, quads[0], quads[1], quads[2]);
            }
        }
        int[] buf = new int[qlen];
        for (int i = 0; i < qlen; i++) {
            buf[i] = quads[i];
        }
        return new NameN(name, hash, buf, qlen);
    }

    static final class Bucket {
        final Name mName;
        final Bucket mNext;

        Bucket(Name name, Bucket next) {
            this.mName = name;
            this.mNext = next;
        }

        public int length() {
            int len = 1;
            for (Bucket curr = this.mNext; curr != null; curr = curr.mNext) {
                len++;
            }
            return len;
        }

        public Name find(int hash, int firstQuad, int secondQuad) {
            if (this.mName.hashCode() == hash && this.mName.equals(firstQuad, secondQuad)) {
                return this.mName;
            }
            for (Bucket curr = this.mNext; curr != null; curr = curr.mNext) {
                Name currName = curr.mName;
                if (currName.hashCode() == hash && currName.equals(firstQuad, secondQuad)) {
                    return currName;
                }
            }
            return null;
        }

        public Name find(int hash, int[] quads, int qlen) {
            if (this.mName.hashCode() == hash && this.mName.equals(quads, qlen)) {
                return this.mName;
            }
            for (Bucket curr = this.mNext; curr != null; curr = curr.mNext) {
                Name currName = curr.mName;
                if (currName.hashCode() == hash && currName.equals(quads, qlen)) {
                    return currName;
                }
            }
            return null;
        }
    }
}
