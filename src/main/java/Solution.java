import java.util.*;

import static java.lang.System.*;
import static java.lang.Math.*;

class SegmentSumTree {
    private long[] add, sum;

    SegmentSumTree(int capacity) {
        --capacity;
        for (int i = 1; i <= 16; i *= 2)
            capacity |= capacity >> i;
        ++capacity;
        add = new long[capacity * 2];
        sum = new long[capacity * 2];
    }

    long get(int from, int to) {
        return get(1, 0, sum.length / 2 - 1, from, to, 0);
    }

    void add(int from, int to, long add) {
        add(1, 0, sum.length / 2 - 1, from, to, add);
    }

    private void add(int node, int l, int r, int from, int to, long val) {
        if (from > r || to < l)      // no intersections
            return;
        if (from <= l && to >= r) {   // covered
            sum[node] += val * (r - l + 1);
            add[node] += val;
            return;
        }
        int mid = (l + r) / 2, leftChild = node * 2, rightChild = node * 2 + 1;
        add(leftChild, l, mid, from, to, val);
        add(rightChild, mid + 1, r, from, to, val);
        sum[node] = sum[leftChild] + sum[rightChild] + add[node] * (r - l + 1);
    }

    private long get(int node, int l, int r, int from, int to, long val) {
        if (from > r || to < l)
            return 0;
        if (from <= l && to >= r)
            return sum[node] + val * (r - l + 1);
        int mid = (l + r) / 2, leftChild = node * 2, rightChild = node * 2 + 1;
        val += add[node];
        return get(leftChild, l, mid, from, to, val) + get(rightChild, mid + 1, r, from, to, val);
    }

    class Node {
        Node l, r, parent;
        int value;

        void setRightChild(Node n) {
            r = n;
            if (n != null)
                n.parent = this;
        }

        void setLeftChild(Node n) {
            l = n;
            if (n != null)
                n.parent = this;
        }

        void replaceChild(Node oldChild, Node newChild) {
            if (l == oldChild)
                l = newChild;
            else if (r == oldChild)
                r = newChild;
            newChild.parent = this;
        }

        void rotateRight() {
            Node g = parent.parent;
            parent.setLeftChild(r);
            setRightChild(parent);
            g.replaceChild(parent, this);
        }

        void rotateLeft() {
            Node g = parent.parent;
            parent.setRightChild(l);
            setLeftChild(parent);
            g.replaceChild(parent, this);
        }

        boolean isRoot() {
            return parent == null || parent.l != this && parent.r != this;
        }

        void propagate(boolean recursive) {
            if (recursive && !isRoot())
                parent.propagate(true);
            if (l != null)
                l.value = value;
            if (r != null)
                r.value = value;
        }

        void splay() {
            propagate(true);
            while (!isRoot()) {
                if (parent.isRoot()) {                  // Zig
                    if (parent.l == this)
                        rotateRight();
                    else
                        rotateLeft();
                } else {
                    if (parent.parent.r == parent) {
                        if (parent.r == this) {
                            parent.rotateLeft();        // Zig-Zig
                            rotateLeft();
                        } else {
                            rotateRight();              // Zig-Zag
                            rotateLeft();
                        }
                    } else {
                        if (parent.l == this) {
                            parent.rotateRight();       // Zig-Zig
                            rotateRight();
                        } else {
                            rotateLeft();               // Zig-Zag
                            rotateRight();
                        }
                    }
                }
            }
        }

        void split(Node d, Node x) {
            splay();                    // x <-- d <-- (y w/o right)
            propagate(false);
            r = null;
            d.parent = this;

            x.splay();
            x.parent = d;
            d.value = x.value;
        }

        void access(int cov) {
            for (Node prev = null, cur = this; cur != null; prev = cur, cur = cur.parent) {
                cur.splay();
                cur.propagate(false);
                cur.r = null;

                Node z = cur;
                while (z.l != null)
                    z = z.l;
                // L=len[fail[z]]+1;
                z.splay();
                cur.splay();

                z = cur;
                while (z.r != null)
                    z = z.r;
                // R=len[z];
                z.splay();
                cur.splay();

                // seg::Do(cur.value,cov,L,R);
                cur.r = prev;
                cur.value = cov;
            }
        }
    }
}

public class Solution {
    private static Scanner sc = new Scanner(in).useLocale(Locale.ROOT);

    public static void main(String[] args) {
//        testRanges();
//        exit(0);

        sc.nextInt();
        int cases = sc.nextInt();
        char[] str = sc.next().toCharArray();
        Map<Integer, Map<Integer, Integer>> queries = new HashMap<>();
        for (int i = 0; i < cases; ++i) {
            int from = sc.nextInt(), to = sc.nextInt();
            queries.computeIfAbsent(to, key -> new HashMap<>()).put(from, i);
        }

        SegmentSumTree ranges = new SegmentSumTree(str.length);
        int[] sa = suffixArray(str);
        int[][] lcp = lcp(sa, str);
        long[] result = new long[cases];
        for (int to = 0; to < str.length; ++to) {
            // all magic there
            ranges.add(0, to, 1);

            for (Map.Entry<Integer, Integer> kv : queries.getOrDefault(to, Collections.emptyMap()).entrySet())
                result[kv.getValue()] = ranges.get(kv.getKey(), to);
        }
        for (long count : result)
            out.printf("%d\n", count);


        StringBuilder sb = new StringBuilder();
        while (cases-- > 0) {
            int offs = sc.nextInt();
            int len = sc.nextInt() + 1 - offs;
            Integer[] pos = new Integer[len];
            for (int i = 0; i < len; ++i)
                pos[i] = offs + i;
            Arrays.sort(pos, (a, b) -> lcp[0][a] - lcp[0][b]);
            long total = len * (len + 1) / 2;
            int prev = pos[0];
            for (int i = 1; i < pos.length; ++i) {
                int l = getLcp(lcp, prev, pos[i], offs + len);
                total -= l;
                if (l != offs + len - pos[i])
                    prev = pos[i];
            }
            sb.append(total).append("\n");
        }
        out.print(sb);
    }

    static void testRanges() {
        long[] arr = new long[10000];
        SegmentSumTree tree = new SegmentSumTree(arr.length);
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            int l = rnd.nextInt(arr.length);
            int r = rnd.nextInt(arr.length);
            int v = rnd.nextInt(10) + 1;
            if (l > r) {
                int t = l;
                l = r;
                r = t;
            }
            tree.add(l, r, v);
//            out.printf("add %d %d %d", l, r, v);
            for (int j = l; j <= r; ++j)
                arr[j] += v;
        }
        for (int i = 0; i < 10000; ++i) {
            int l = rnd.nextInt(arr.length);
            int r = rnd.nextInt(arr.length);
            if (l > r) {
                int t = l;
                l = r;
                r = t;
            }
            long v = tree.get(l, r);
            long t = 0;
            for (int j = l; j <= r; ++j)
                t += arr[j];
            if (v != t)
                out.printf("%d..%d: %d/%d\n", l, r, v, t);
        }

    }

    private static int[] suffixArray(char[] str) {
        Integer[] boxed = new Integer[str.length];
        int[] sa = new int[str.length], classes = new int[str.length];
        for (int i = 0; i < str.length; ++i) {
            boxed[i] = boxed.length - 1 - i;
            classes[i] = str[i];
        }
        Arrays.sort(boxed, (a, b) -> Character.compare(str[a], str[b]));
        for (int i = 0; i < str.length; ++i)
            sa[i] = boxed[i];
        for (int len = 1; len < str.length; len *= 2) {
            int[] c = classes.clone();
            for (int i = 0; i < str.length; i++)
                classes[sa[i]] = i > 0 && c[sa[i - 1]] == c[sa[i]] && sa[i - 1] + len < str.length && c[sa[i - 1] + len / 2] == c[sa[i] + len / 2] ? classes[sa[i - 1]] : i;
            int[] cnt = new int[str.length];
            for (int i = 0; i < str.length; ++i)
                cnt[i] = i;
            int[] s = sa.clone();
            for (int i = 0; i < str.length; ++i) {
                int s1 = s[i] - len;
                if (s1 >= 0)
                    sa[cnt[classes[s1]]++] = s1;
            }
        }
        return sa;
    }

    private static int getLcp(int[][] lcp, int from, int to, int strip) {
        int sa1 = min(lcp[0][from], lcp[0][to]);
        int sa2 = max(lcp[0][from], lcp[0][to]);
        int log = lcp[1][sa2 - sa1];
        return min(strip - max(from, to), min(lcp[log][sa1], lcp[log][sa2 - (1 << (log - 2))]));
    }

    private static int[][] lcp(int[] sa, char[] str) {
        int[] revSa = new int[sa.length], log = new int[sa.length], lcp1 = new int[sa.length - 1];
        for (int i = 0; i < revSa.length; ++i) {
            revSa[sa[i]] = i;
            log[i] = log[i / 2] + 1;
        }
        for (int i = 0, curLcp = 0; i < str.length; ++i) {
            int saPtr = revSa[i];
            if (saPtr != lcp1.length) {
                int j = sa[saPtr + 1];
                for (int top = str.length - max(i, j); curLcp < top; ++curLcp)
                    if (str[i + curLcp] != str[j + curLcp])
                        break;
                lcp1[saPtr] = curLcp;
                if (i < sa.length - 1 && j < sa.length - 1 && revSa[i + 1] + 1 == revSa[j + 1] && curLcp > 0)
                    --curLcp;
                else
                    curLcp = 0;
            } else {
                curLcp = 0;
            }
        }
        int[][] lcp = new int[log[log.length - 1] + 1][];
        lcp[0] = revSa;
        lcp[1] = log;
        lcp[2] = lcp1;
        for (int i = 3; i < lcp.length; ++i) {
            lcp[i] = new int[lcp[i - 1].length - (1 << (i - 3))];
            for (int j = 0; j < lcp[i].length; ++j)
                lcp[i][j] = min(lcp[i - 1][j], lcp[i - 1][j + (1 << (i - 3))]);
        }
        return lcp;
    }

}

/*
#include<bits/stdc++.h>
#define pii pair<int,int>
#define fi first
#define se second
#define pb push_back
#define SZ(x) ((int)((x).size()))
#define rep(i,j,k) for(int i=(int)j;i<=(int)k;i++)
#define per(i,j,k) for(int i=(int)j;i>=(int)k;i--)
using namespace std;
typedef long long LL;
typedef double DB;
const DB pi=acos(-1.0);
const int N=100005;
int go[N<<1][26],fail[N<<1],len[N<<1],tot,last;
int n;
int cnt=0;
namespace seg{
    int tag[N<<2];
    LL sum[N<<2];
    inline void Tag(int me,int l,int r,int v){
        tag[me]+=v;
        sum[me]+=(r-l+1)*1ll*v;
    }
    inline void down(int me,int l,int r){
        if(tag[me]==0)return;
        int mid=(l+r)>>1;
        Tag(me<<1,l,mid,tag[me]);
        Tag(me<<1|1,mid+1,r,tag[me]);
        tag[me]=0;
    }
    void add(int me,int l,int r,int x,int y,int v){
        //if(l==1&&r==n)printf("add %d %d %d\n",x,y,v);
        if(l^r)down(me,l,r);
        if(x<=l&&r<=y){
            Tag(me,l,r,v);
            return;
        }
        int mid=(l+r)>>1;
        if(x<=mid)add(me<<1,l,mid,x,y,v);
        if(y>mid)add(me<<1|1,mid+1,r,x,y,v);
        sum[me]=sum[me<<1]+sum[me<<1|1];
    }
    LL ask(int me,int l,int r,int x,int y){
        if(l^r)down(me,l,r);
        if(x<=l&&r<=y)return sum[me];

        int mid=(l+r)>>1;
        LL ret=0;
        if(x<=mid)ret+=ask(me<<1,l,mid,x,y);
        if(y>mid)ret+=ask(me<<1|1,mid+1,r,x,y);
        return ret;
    }
    void Do(int pre,int now,int L,int R){
        if(L>R)return;
        ++cnt;
        //printf("_%d %d %d %d\n",pre,now,L,R);

        // decrement

        if(pre)add(1,1,n,pre-R+1,pre-L+1,-1);
        add(1,1,n,now-R+1,now-L+1,1);
    }
};
namespace lct{
    void Access(int x,int cov){
        int y=0;
        for(;x;y=x,x=fa[x]){
            splay(x);
            down(x);
            r[x]=0;

            int L,R;
            int z=x;
            while(l[z])z=l[z];
            L=len[fail[z]]+1;
            splay(z);splay(x);
            z=x;
            while(r[z])z=r[z];
            R=len[z];
            splay(z);splay(x);
            seg::Do(last[x],cov,L,R);
            r[x]=y;
            last[x]=cov;
        }
    }
    void SetFa(int x,int y,int po){
        fa[x]=y;
        Access(x,po);
    }
    void split(int x,int y,int d){
        splay(y);
        down(y);
        r[y]=0;
        fa[d]=y;
        splay(x);
        fa[x]=d;
        last[d]=last[x];
    }
};
namespace sam{
    void init(){
        tot=last=1;
    }
    void expended(int x,int po){
        int gt=++tot;
        len[gt] = len[last] + 1;
        int p = last;
        last = tot;

        for( ; p && (!go[p][x]); p = fail[p])
          go[p][x]=gt;

        if(!p){
            fail[gt]=1;
            lct::SetFa(gt,1,po);
            return;
        }
        int xx=go[p][x];
        if(len[xx]==len[p]+1){
            fail[gt]=xx;
            lct::SetFa(gt,xx,po);
            return;
        }
        int tt=++tot;
        len[tt]=len[p]+1;
        fail[tt]=fail[xx];
        int dt=fail[xx];
        fail[xx]=fail[gt]=tt;
        lct::split(xx,dt,tt);
        lct::SetFa(gt,tt,po);
        rep(i,0,25)go[tt][i]=go[xx][i];
        for(;p&&(go[p][x]==xx);p=fail[p])go[p][x]=tt;
    }
};
int Q;
char str[N];
int qL[N];
vector<int>que[N];
LL ans[N];
void Main(){
    rep(i,1,n){
        sam::expended(str[i]-'a',i);
        rep(j,0,que[i].size()-1){
            int id=que[i][j];
            ans[id]=seg::ask(1,1,n,qL[id],n);
        }
    }
}
void init(){
    scanf("%d%d",&n,&Q);
    scanf("%s",str+1);
    rep(i,1,Q){
        int r;scanf("%d%d",&qL[i],&r);
        qL[i]++;r++;
        que[r].pb(i);
    }
    sam::init();
}
void Output(){
    rep(i,1,Q)printf("%lld\n",ans[i]);
}
int main(){
    init();
    Main();
    Output();
    return 0;
}
 */