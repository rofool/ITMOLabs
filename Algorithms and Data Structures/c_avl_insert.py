import sys


class Node:
    __slots__ = ('key', 'left', 'right', 'height')

    def __init__(self, key):
        self.key = key
        self.left = self.right = None
        self.height = 1


def height(v):
    return v.height if v is not None else 0


def update(v):
    v.height = 1 + max(height(v.left), height(v.right))


def balance_factor(v):
    return height(v.right) - height(v.left)


def rotate_left(a):
    b = a.right
    a.right = b.left
    b.left = a
    update(a)
    update(b)
    return b


def rotate_right(a):
    b = a.left
    a.left = b.right
    b.right = a
    update(a)
    update(b)
    return b


def balance(v):
    update(v)
    factor = balance_factor(v)
    if factor == 2:
        if balance_factor(v.right) < 0:
            v.right = rotate_right(v.right)
        return rotate_left(v)
    if factor == -2:
        if balance_factor(v.left) > 0:
            v.left = rotate_left(v.left)
        return rotate_right(v)
    return v


def add(v, x):
    if v is None:
        return Node(x)
    if x < v.key:
        v.left = add(v.left, x)
    elif x > v.key:
        v.right = add(v.right, x)
    else:
        return v
    return balance(v)


def exists(v, x):
    while v is not None:
        if x == v.key:
            return True
        v = v.left if x < v.key else v.right
    return False


def preorder(root):
    if root is None:
        return 'Empty'
    result = []
    stack = [root]
    while stack:
        v = stack.pop()
        result.append(str(v.key))
        if v.right is not None:
            stack.append(v.right)
        if v.left is not None:
            stack.append(v.left)
    return ' '.join(result)


def main():
    input = sys.stdin.buffer.readline
    q = int(input())
    root = None
    answer = []
    for _ in range(q):
        parts = input().split()
        command = parts[0]
        if command == b'add':
            root = add(root, int(parts[1]))
        elif command == b'exists':
            answer.append('YES' if exists(root, int(parts[1])) else 'NO')
        elif command == b'root':
            answer.append(str(root.key) if root is not None else 'Empty')
        elif command == b'height':
            answer.append(str(height(root)))
        elif command == b'print':
            answer.append(preorder(root))
    sys.stdout.write('\n'.join(answer))


if __name__ == '__main__':
    main()
